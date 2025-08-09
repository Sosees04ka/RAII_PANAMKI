from typing import Optional, List

from models.cloth import Cloth


class Outfit:
    def __init__(self, top: Cloth, bottom: Optional[Cloth],
                 footwear: Cloth, accessory: Cloth):
        self.top = top
        self.bottom = bottom
        self.footwear = footwear
        self.accessory = accessory
        self._score = 0.0

    @property
    def score(self) -> float:
        return self._score

    @score.setter
    def score(self, value: float):
        self._score = value

    @property
    def items(self) -> List[Cloth]:
        return [item for item in [self.top, self.bottom, self.footwear, self.accessory] if item is not None]

    def __repr__(self):
        items_str = "\n".join(f"   - {item}" for item in self.items)
        return f"Outfit (Score: {self.score:.2f}):\n{items_str}"
