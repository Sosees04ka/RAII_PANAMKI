from typing import List, Tuple, Optional

from models.cloth import Cloth
from models.outfit import Outfit
from services.outfit_generator import OutfitGenerator


class OutfitRecommender:
    def __init__(self, outfit_generator: OutfitGenerator):
        self.generator = outfit_generator
        self._outfits = None
        self._scored_outfits = None

    @property
    def outfits(self) -> List[Outfit]:
        if self._outfits is None:
            self._outfits = self.generator.generate_outfits()
        return self._outfits

    @property
    def scored_outfits(self) -> List[Tuple[float, Outfit]]:
        if self._scored_outfits is None:
            self._scored_outfits = []
            for outfit in self.outfits:
                score = self.generator.score_outfit(outfit)
                outfit.score = score
                self._scored_outfits.append((score, outfit))
            self._scored_outfits.sort(key=lambda x: x[0], reverse=True)
        return self._scored_outfits

    def get_top_outfits_with_item(self, item_id: Optional[int], top_n: int = 10) -> List[Outfit]:
        item = next((i for i in self.generator.wardrobe if i.id == item_id), None)
        if not item:
            print(f"Вещь с id={item_id} не найдена.")
            return []

        print(f"Выбранная вещь: {item}")

        filtered = [
            outfit for score, outfit in self.scored_outfits
            if any(i.id == item_id for i in outfit.items)
        ]

        if not filtered:
            print("Образы с этой вещью не найдены.")
            return []

        return filtered[:top_n]


def initialize_wardrobe(raw_wardrobe: List[dict]) -> List[Cloth]:
    wardrobe = []
    for item in raw_wardrobe:
        try:
            wardrobe.append(Cloth(
                id=item["id"],
                masterCategory=item["masterCategory"],
                subCategory=item["subCategory"],
                color=item["color"],
                usage=item["usage"],
                imageBase64=item.get("imageBase64", "")
            ))
        except Exception as e:
            raise ValueError(f"Invalid item data: {item}. Error: {str(e)}")
    return wardrobe