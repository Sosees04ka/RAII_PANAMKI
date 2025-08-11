from fastapi import FastAPI, HTTPException

from models.wardrobe_request import ClothRequest, ClothingItem, BaseRequest
from services.AI_classifier import classify_style, classify_masterCategory, classify_subCategory
from services.dominant_color_algorithm import get_color
from services.fashion_rules import FashionRules
from services.outfit_generator import OutfitGenerator
from services.outfit_recommender import initialize_wardrobe, OutfitRecommender

app = FastAPI()

@app.post("/add_cloth_to_wardrobe")
async def add_cloth_to_wardrobe(img64: str):
    try:
        dominant_color = get_color(img64)
        masterCategory = classify_masterCategory(img64)
        subCategory = classify_subCategory(masterCategory, img64)
        style = classify_style(img64)
        return {
            "masterCategory": masterCategory,
            "subCategory": subCategory,
            "color": dominant_color if isinstance(dominant_color, list) else list(dominant_color),
            "usage": style,
            "imageBase64": img64
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
        img64 = request.imageBase64
        color = get_color(img64)

        masterCategory = classify_masterCategory(img64)
        subCategory = classify_subCategory(masterCategory, img64)
        usage = classify_style(img64)

        cloth = ClothingItem(
            id=None,
            masterCategory=masterCategory,
            subCategory=subCategory,
            color=color,
            usage=usage,
            imageBase64=img64
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
