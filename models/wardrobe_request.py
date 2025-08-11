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

class WeatherDescription(BaseModel):
    daytime: str
    obs_time: int
    season: str
    source: str
    uptime: int
    cloudness: int
    condition: str
    feels_like: int
    humidity: int
    icon: str
    is_thunder: bool
    polar: bool
    prec_prob: int
    prec_strength: int
    prec_type: int
    pressure_mm: int
    pressure_pa: int
    temp: int
    uv_index: int
    visibility: int
    wind_angle: int
    wind_dir: str
    wind_gust: float
    wind_speed: float

class WardrobeWeatherRequest(BaseModel):
    wardrobe: List[ClothingItem]
    weather: WeatherDescription