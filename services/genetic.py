import random
from typing import List, Tuple, Dict
from models.cloth import Cloth
from models.outfit import Outfit
from services.fashion_rules import FashionRules
import itertools
from colorsys import rgb_to_hls


class GeneticOutfitGenerator:
    def __init__(self, wardrobe: List[Cloth], fashion_rules: FashionRules, target_cloth: Cloth):
        self.wardrobe = [item for item in wardrobe if isinstance(item, Cloth)]
        self.fashion_rules = fashion_rules
        if not isinstance(target_cloth, Cloth):
            raise ValueError("target_cloth must be a Cloth object")
        self.target_cloth = target_cloth
        self.population_size = 80
        self.generations = 15
        self.mutation_rate = 0.2
        self.elitism_count = 10

        self._categorized_wardrobe = self._initialize_categorized_wardrobe()

    def _initialize_categorized_wardrobe(self) -> Dict[str, List[Cloth]]:
        categorized = {
            "Topwear": [],
            "Bottomwear": [],
            "Footwear": [],
            "Accessories": []
        }

        for item in self.wardrobe:
            if not isinstance(item, Cloth):
                continue

            if item.masterCategory == "Topwear":
                categorized["Topwear"].append(item)
            elif item.masterCategory == "Bottomwear":
                categorized["Bottomwear"].append(item)
            elif item.masterCategory == "Footwear":
                categorized["Footwear"].append(item)
            elif item.masterCategory == "Accessories":
                categorized["Accessories"].append(item)

        return categorized


    def generate_outfits(self) -> List[Outfit]:
        outfits = self._generate_regular_outfits()

        return sorted(outfits, key=lambda x: x.score, reverse=True)[:10]

    def _generate_regular_outfits(self) -> List[Outfit]:
        try:
            population = self._initialize_population()

            for _ in range(self.generations):
                scored_population = [(self._score_outfit(outfit), outfit) for outfit in population]
                scored_population.sort(key=lambda x: x[0], reverse=True)

                elites = [outfit for score, outfit in scored_population[:self.elitism_count]]
                next_generation = elites.copy()

                while len(next_generation) < self.population_size:
                    parent1 = self._select_parent(scored_population)
                    parent2 = self._select_parent(scored_population)
                    child = self._crossover(parent1, parent2)
                    child = self._mutate(child)
                    next_generation.append(child)

                population = next_generation

            unique_outfits = []
            seen_outfits = set()

            for outfit in population:
                outfit_key = self._get_outfit_key(outfit)
                if outfit_key not in seen_outfits:
                    seen_outfits.add(outfit_key)
                    outfit.score = self._score_outfit(outfit)
                    unique_outfits.append(outfit)
                    if len(unique_outfits) >= 10:
                        break

            return unique_outfits

        except Exception as e:
            print(f"Ошибка при генерации обычных образов: {str(e)}")
            return []

    def _initialize_population(self) -> List[Outfit]:
        population = []
        target_category = self.target_cloth.masterCategory
        special_categories = {"Dresses", "Tracksuits", "Jumpsuit"}

        for _ in range(self.population_size):
            try:
                if target_category == "Topwear":
                    top = self.target_cloth
                    if top.subCategory in special_categories:
                        bottom = None
                    else:
                        bottom = random.choice(self._categorized_wardrobe["Bottomwear"])

                    footwear = random.choice(self._categorized_wardrobe["Footwear"])
                    accessory = random.choice(self._categorized_wardrobe["Accessories"])

                    outfit = Outfit(top, bottom, footwear, accessory)

                elif target_category == "Bottomwear":
                    bottom = self.target_cloth
                    top = random.choice(self._categorized_wardrobe["Topwear"])
                    if top.subCategory in special_categories:
                        bottom = None
                    footwear = random.choice(self._categorized_wardrobe["Footwear"])
                    accessory = random.choice(self._categorized_wardrobe["Accessories"])

                    outfit = Outfit(top, bottom, footwear, accessory)

                elif target_category == "Footwear":
                    footwear = self.target_cloth
                    top = random.choice(self._categorized_wardrobe["Topwear"])
                    if top.subCategory in special_categories:
                        bottom = None
                    else:
                        bottom = random.choice(self._categorized_wardrobe["Bottomwear"])
                    accessory = random.choice(self._categorized_wardrobe["Accessories"])

                    outfit = Outfit(top, bottom, footwear, accessory)

                elif target_category == "Accessories":
                    accessory = self.target_cloth
                    top = random.choice(self._categorized_wardrobe["Topwear"])
                    if top.subCategory in special_categories:
                        bottom = None
                    else:
                        bottom = random.choice(self._categorized_wardrobe["Bottomwear"])
                    footwear = random.choice(self._categorized_wardrobe["Footwear"])

                    outfit = Outfit(top, bottom, footwear, accessory)

                else:
                    continue

                population.append(outfit)
            except Exception as e:
                continue

        return population

    def _select_parent(self, scored_population: List[Tuple[float, Outfit]]) -> Outfit:
        tournament = random.sample(scored_population, min(3, len(scored_population)))
        return max(tournament, key=lambda x: x[0])[1]

    def _crossover(self, parent1: Outfit, parent2: Outfit) -> Outfit:
        special_categories = {"Dresses", "Tracksuits", "Jumpsuit"}

        child_items = {
            'top': parent1.top if random.random() < 0.5 else parent2.top,
            'bottom': parent1.bottom if random.random() < 0.5 else parent2.bottom,
            'footwear': parent1.footwear if random.random() < 0.5 else parent2.footwear,
            'accessory': parent1.accessory if random.random() < 0.5 else parent2.accessory
        }

        target_category = self.target_cloth.masterCategory
        if target_category == "Topwear":
            child_items['top'] = self.target_cloth
            if self.target_cloth.subCategory in special_categories:
                child_items['bottom'] = None
        elif target_category == "Bottomwear":
            child_items['bottom'] = self.target_cloth
            if child_items['top'] and child_items['top'].subCategory in special_categories:
                child_items['bottom'] = None
        elif target_category == "Footwear":
            child_items['footwear'] = self.target_cloth
        elif target_category == "Accessories":
            child_items['accessory'] = self.target_cloth

        if child_items['top'] and child_items['top'].subCategory in special_categories:
            child_items['bottom'] = None

        return Outfit(**child_items)

    def _mutate(self, outfit: Outfit) -> Outfit:
        special_categories = {"Dresses", "Tracksuits", "Jumpsuit"}

        if random.random() < self.mutation_rate:
            item_to_mutate = random.choice(['top', 'bottom', 'footwear', 'accessory'])

            target_category = self.target_cloth.masterCategory
            if (item_to_mutate == 'top' and target_category == "Topwear") or \
                    (item_to_mutate == 'bottom' and target_category == "Bottomwear") or \
                    (item_to_mutate == 'footwear' and target_category == "Footwear") or \
                    (item_to_mutate == 'accessory' and target_category == "Accessories"):
                return outfit

            if item_to_mutate == 'top' and self._categorized_wardrobe["Topwear"]:
                new_top = random.choice(self._categorized_wardrobe["Topwear"])
                if new_top.subCategory in special_categories:
                    outfit.bottom = None
                outfit.top = new_top

            elif item_to_mutate == 'bottom' and self._categorized_wardrobe["Bottomwear"]:
                if outfit.top and outfit.top.subCategory not in special_categories:
                    outfit.bottom = random.choice(self._categorized_wardrobe["Bottomwear"])

            elif item_to_mutate == 'footwear' and self._categorized_wardrobe["Footwear"]:
                outfit.footwear = random.choice(self._categorized_wardrobe["Footwear"])

            elif item_to_mutate == 'accessory' and self._categorized_wardrobe["Accessories"]:
                outfit.accessory = random.choice(self._categorized_wardrobe["Accessories"])

        return outfit

    def _get_outfit_key(self, outfit: Outfit) -> Tuple:
        return tuple(
            (item.id if item else None)
            for item in [outfit.top, outfit.bottom, outfit.footwear, outfit.accessory]
        )

    def _score_outfit(self, outfit: Outfit) -> float:
        items = [item for item in [outfit.top, outfit.bottom, outfit.footwear, outfit.accessory] if item]

        if not items:
            return 0.0

        color_score = self._calculate_color_score(items)
        style_score = self._calculate_style_score(items)
        conflict_score = self._calculate_conflict_score(items)

        return color_score * 0.4 + style_score * 0.4 + conflict_score * 0.2

    def _calculate_color_score(self, items: List[Cloth]) -> float:
        colors = [item.color for item in items]
        pairs = list(itertools.combinations(colors, 2))

        if not pairs:
            return 0.0

        total = sum(self._color_pair_score(a, b) for a, b in pairs)
        return total / len(pairs)

    def _color_pair_score(self, c1: Tuple[int, int, int], c2: Tuple[int, int, int]) -> float:
        h1 = self._rgb_to_hue(c1)
        h2 = self._rgb_to_hue(c2)
        diff = abs(h1 - h2)
        return 1 - min(diff, 360 - diff) / 180

    def _rgb_to_hue(self, rgb: Tuple[int, int, int]) -> float:
        r, g, b = (x / 255 for x in rgb)
        h, _, _ = rgb_to_hls(r, g, b)
        return h * 360

    def _calculate_style_score(self, items: List[Cloth]) -> float:
        styles = [item.usage for item in items]
        pairs = list(itertools.combinations(styles, 2))

        if not pairs:
            return 0.0

        total = sum(self.fashion_rules.style_pairs.get((a, b), 0.5) for a, b in pairs)
        return total / len(pairs)

    def _calculate_conflict_score(self, items: List[Cloth]) -> float:
        subs = [item.subCategory for item in items]
        pairs = list(itertools.combinations(subs, 2))

        conflict_score = 1.0
        for a, b in pairs:
            score = self.fashion_rules.fashion_conflicts.get((a, b), 0)
            if score < 0:
                return 0.0
            conflict_score *= (1 + score) / 2

        return conflict_score


class OutfitRecommenderGA:
    def __init__(self, wardrobe: List[Cloth], fashion_rules: FashionRules):
        self.wardrobe = [item for item in wardrobe if isinstance(item, Cloth)]
        self.fashion_rules = fashion_rules

    def get_top_outfits_with_item(self, target_cloth, top_n: int = 10, min_score: float = 0.3) -> List[Outfit]:
        if not isinstance(target_cloth, Cloth):
            try:
                target_cloth = self._convert_to_cloth(target_cloth)
            except ValueError as e:
                print(f"Ошибка преобразования target_cloth: {e}")
                return []

        if target_cloth not in self.wardrobe:
            self.wardrobe.append(target_cloth)

        print(f"Выбранная вещь: {target_cloth}")

        generator = GeneticOutfitGenerator(
            wardrobe=self.wardrobe,
            fashion_rules=self.fashion_rules,
            target_cloth=target_cloth
        )

        outfits = generator.generate_outfits()

        filtered_outfits = [outfit for outfit in outfits if outfit.score >= min_score]

        if not filtered_outfits:
            print(f"Нет образов с score >= {min_score}, возвращаем лучшие доступные")
            return sorted(outfits, key=lambda x: x.score, reverse=True)[:min(3, len(outfits))]

        return filtered_outfits[:top_n]

    def _convert_to_cloth(self, cloth_data) -> Cloth:
        if isinstance(cloth_data, dict):
            try:
                color = cloth_data.get("color", (0, 0, 0))
                if isinstance(color, str):
                    color = tuple(map(int, color.split(',')))
                elif isinstance(color, list):
                    color = tuple(color[:3])

                return Cloth(
                    id=cloth_data.get("id"),
                    masterCategory=cloth_data.get("masterCategory", ""),
                    subCategory=cloth_data.get("subCategory", ""),
                    color=color,
                    usage=cloth_data.get("usage", ""),
                    imageBase64=cloth_data.get("imageBase64", "")
                )
            except Exception as e:
                raise ValueError(f"Не удалось преобразовать данные в объект Cloth: {e}")
        elif isinstance(cloth_data, Cloth):
            return cloth_data
        else:
            raise ValueError("Неподдерживаемый тип данных для target_cloth")
