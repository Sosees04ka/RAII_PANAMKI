from typing import List, Tuple, Optional
from pydantic import BaseModel, validator


class ClothingItem(BaseModel):
    id: Optional[int] = None
    masterCategory: str
    subCategory: str
    color: Tuple[int, int, int]
    usage: str
    imageBase64: str

    @validator('color', pre=True)
    def parse_color(cls, v):
        if isinstance(v, str):
            return tuple(map(int, v.split(',')))
        elif isinstance(v, list):
            return tuple(v)
        return v

class ClothRequest(BaseModel):
    cloth: ClothingItem
    wardrobe: List[ClothingItem]

class BaseRequest(BaseModel):
    imageBase64: str
    wardrobe: List[ClothingItem]

class ImageRequest(BaseModel):
    img64: str