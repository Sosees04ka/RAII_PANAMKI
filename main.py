from fastapi import FastAPI, HTTPException

from models.wardrobe_request import ClothRequest, ClothingItem, BaseRequest
from services.dominant_color_algorithm import get_color
from services.fashion_rules import FashionRules
from services.outfit_generator import OutfitGenerator
from services.outfit_recommender import initialize_wardrobe, OutfitRecommender

app = FastAPI()


@app.get("/")
async def root():
    return {"message": "Hello World"}


@app.get("/hello/{name}")
async def say_hello(name: str):
    return {"message": f"Hello {name}"}


@app.post("/test_color")
async def test_color(str_img: str):
    try:
        dominant_color = get_color(str_img)
        return {
            "dominant_color": {
                "red": int(dominant_color[0]),
                "green": int(dominant_color[1]),
                "blue": int(dominant_color[2])
            }
        }
    except HTTPException as e:
        raise e
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")


@app.post("/generate_outfit")
async def generate_outfit(request: ClothRequest):
    try:
        raw_wardrobe = [request.cloth.dict()] + [item.dict() for item in request.wardrobe]

        wardrobe = initialize_wardrobe(raw_wardrobe)

        fashion_rules = FashionRules()
        fashion_rules.load_conflicts("./util/conflicts.json")

        generator = OutfitGenerator(wardrobe, fashion_rules)
        recommender = OutfitRecommender(generator)

        main_item_id = wardrobe[0].id
        top_outfits = recommender.get_top_outfits_with_item(main_item_id)

        generated_outfits = []
        for outfit in top_outfits:
            outfit_items = []
            for item in outfit.items:
                outfit_items.append(
                    ClothingItem(
                        id=item.id,
                        masterCategory=item.masterCategory,
                        subCategory=item.subCategory,
                        color=','.join(map(str, item.color)) if isinstance(item.color, tuple) else item.color,
                        usage=item.usage,
                        imageBase64=item.imageBase64
                    )
                )

            generated_outfits.append({
                "score": outfit.score,
                "items": outfit_items
            })

        return {
            "status": "success",
            "main_item": wardrobe[0],
            "generated_outfits": generated_outfits
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/generate_outfit_with_base64")
async def generate_outfit_with_base64(request: BaseRequest):
    try:
        color = get_color(request.imageBase64)

        subCategory = "Shorts"
        masterCategory = "bottomwear"
        usage = "Sports"
        imageBase64 = request.imageBase64

        cloth = ClothingItem(
            id=None,
            masterCategory=masterCategory,
            subCategory=subCategory,
            color=color,
            usage=usage,
            imageBase64=imageBase64
        )

        wardrobe_items = [item.dict() for item in request.wardrobe]
        raw_wardrobe = [cloth.dict()] + wardrobe_items

        wardrobe = initialize_wardrobe(raw_wardrobe)

        fashion_rules = FashionRules()
        fashion_rules.load_conflicts("./util/conflicts.json")

        generator = OutfitGenerator(wardrobe, fashion_rules)
        recommender = OutfitRecommender(generator)

        top_outfits = recommender.get_top_outfits_with_item(None)

        generated_outfits = []
        for outfit in top_outfits:
            outfit_items = []
            for item in outfit.items:
                outfit_items.append(
                    {
                        "id": item.id,
                        "masterCategory": item.masterCategory,
                        "subCategory": item.subCategory,
                        "color": item.color if isinstance(item.color, list) else list(item.color),
                        "usage": item.usage,
                        "imageBase64": item.imageBase64
                    }
                )

            generated_outfits.append({
                "score": outfit.score,
                "items": outfit_items
            })

        return {
            "status": "success",
            "main_item": {
                "id": wardrobe[0].id,
                "masterCategory": wardrobe[0].masterCategory,
                "subCategory": wardrobe[0].subCategory,
                "color": wardrobe[0].color if isinstance(wardrobe[0].color, list) else list(wardrobe[0].color),
                "usage": wardrobe[0].usage,
                "imageBase64": wardrobe[0].imageBase64
            },
            "generated_outfits": generated_outfits
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
