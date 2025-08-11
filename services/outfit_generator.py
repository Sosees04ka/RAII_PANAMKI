import itertools
from colorsys import rgb_to_hls
from typing import List, Dict, Tuple

from models.cloth import Cloth
from models.outfit import Outfit
from services.fashion_rules import FashionRules


class OutfitGenerator:
    def __init__(self, wardrobe: List[Cloth], fashion_rules: FashionRules):
        self.wardrobe = wardrobe
        self.fashion_rules = fashion_rules
        self._categorized_wardrobe = None

    @property
    def categorized_wardrobe(self) -> Dict[str, List[Cloth]]:
        if self._categorized_wardrobe is None:
            self._categorized_wardrobe = {
                "Topwear": [i for i in self.wardrobe if i.masterCategory == "Topwear"],
                "Bottomwear": [i for i in self.wardrobe if i.masterCategory == "Bottomwear"],
                "Footwear": [i for i in self.wardrobe if i.masterCategory == "Footwear"],
                "Accessories": [i for i in self.wardrobe if i.masterCategory == "Accessories"]
            }
        return self._categorized_wardrobe

    def generate_outfits(self) -> List[Outfit]:
        outfits = []
        categories = self.categorized_wardrobe

        for top in categories["Topwear"]:
            if top.subCategory in ["Dresses", "Jumpsuit", "Tracksuits"]:
                for shoes in categories["Footwear"]:
                    for accessory in categories["Accessories"]:
                        outfits.append(Outfit(top, None, shoes, accessory))
            else:
                for bottom in categories["Bottomwear"]:
                    for shoes in categories["Footwear"]:
                        for accessory in categories["Accessories"]:
                            outfits.append(Outfit(top, bottom, shoes, accessory))

        return outfits

    def score_outfit(self, outfit: Outfit) -> float:
        items = outfit.items
        if not items:
            return 0.0

        color_score = self._calculate_color_score(items)
        style_score = self._calculate_style_score(items)
        conflict_score = self._calculate_conflict_score(items)

        return color_score * 0.4 + style_score * 0.4 + conflict_score * 0.2

    def _calculate_color_score(self, items: List[Cloth]) -> float:
        colors = [i.color for i in items]
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
        styles = [i.usage for i in items]
        pairs = list(itertools.combinations(styles, 2))
        if not pairs:
            return 0.0

        total = sum(self.fashion_rules.style_pairs.get((a, b), 0.5) for a, b in pairs)
        return total / len(pairs)

    def _calculate_conflict_score(self, items: List[Cloth]) -> float:
        subs = [i.subCategory for i in items]
        pairs = list(itertools.combinations(subs, 2))

        conflict_score = 1.0
        for a, b in pairs:
            score = self.fashion_rules.fashion_conflicts.get((a, b), 0)
            if score < 0:
                return 0.0
            conflict_score *= (1 + score) / 2

        return conflict_score