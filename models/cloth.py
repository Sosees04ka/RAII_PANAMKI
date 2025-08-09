from typing import Tuple, Optional


class Cloth:
    def __init__(self, id: Optional[int], masterCategory: str, subCategory: str,
                 color: Tuple[int, int, int], usage: str, imageBase64: str):
        self.id = id
        self.masterCategory = masterCategory
        self.subCategory = subCategory
        self.color = color
        self.usage = usage
        self.imageBase64 = imageBase64

    def __repr__(self):
        return f"{self.subCategory} ({self.usage})"