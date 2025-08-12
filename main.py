from fastapi import FastAPI, HTTPException

from models.wardrobe_request import ClothRequest, ClothingItem, BaseRequest, ImageRequest, WardrobeWeatherRequest
from services.AI_classifier import predict_style, predict_masterCategory, predict_subCategory
from services.dominant_color_algorithm import get_color
from services.fashion_rules import FashionRules
from services.genetic import OutfitRecommenderGA
from services.outfit_generator import OutfitGenerator
from services.outfit_recommender import initialize_wardrobe, OutfitRecommender
from services.llm_weather import ask_gigachat, extract_json_from_text

app = FastAPI()

@app.post("/add_cloth_to_wardrobe")
async def add_cloth_to_wardrobe(request: ImageRequest):
    try:
        img64 = request.img64
        dominant_color = get_color(img64)
        masterCategory = predict_masterCategory(img64)
        style = predict_style(img64)
        subCategory = predict_subCategory(img64, masterCategory)
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


@app.post("/generate_outfit_ga")
async def generate_outfit_ga(request: ClothRequest):
    try:
        raw_wardrobe = [request.cloth.dict()] + [item.dict() for item in request.wardrobe]
        wardrobe = initialize_wardrobe(raw_wardrobe)

        fashion_rules = FashionRules()
        fashion_rules.load_conflicts("./util/conflicts.json")

        recommender = OutfitRecommenderGA(wardrobe, fashion_rules)
        target_cloth = wardrobe[0]
        top_outfits = recommender.get_top_outfits_with_item(target_cloth)

        generated_outfits = []
        for outfit in top_outfits:
            outfit_items = []
            for item in outfit.items:
                outfit_items.append({
                    "id": item.id,
                    "masterCategory": item.masterCategory,
                    "subCategory": item.subCategory,
                    "color": list(item.color),
                    "usage": item.usage,
                    "imageBase64": item.imageBase64
                })

            generated_outfits.append({
                "score": outfit.score,
                "items": outfit_items
            })

        return {
            "status": "success",
            "main_item": {
                "id": target_cloth.id,
                "masterCategory": target_cloth.masterCategory,
                "subCategory": target_cloth.subCategory,
                "color": list(target_cloth.color),
                "usage": target_cloth.usage,
                "imageBase64": target_cloth.imageBase64
            },
            "generated_outfits": generated_outfits
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/generate_outfit_with_base64")
async def generate_outfit_with_base64(request: BaseRequest):
    try:
        img64 = request.imageBase64
        dominant_color = get_color(img64)
        masterCategory = predict_masterCategory(img64)
        subCategory = predict_subCategory(img64, masterCategory)

        style = predict_style(img64)

        cloth = ClothingItem(
            id=None,
            masterCategory=masterCategory,
            subCategory=subCategory,
            color=dominant_color,
            usage=style,
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

@app.post("/generate_outfit_with_base64_ga")
async def generate_outfit_with_base64_ga(request: BaseRequest):
    try:
        img64 = request.imageBase64
        dominant_color = get_color(img64)
        masterCategory = predict_masterCategory(img64)
        subCategory = predict_subCategory(img64, masterCategory)

        style = predict_style(img64)

        cloth = ClothingItem(
            id=None,
            masterCategory=masterCategory,
            subCategory=subCategory,
            color=dominant_color,
            usage=style,
            imageBase64=img64
        )

        wardrobe_items = [item.dict() for item in request.wardrobe]
        raw_wardrobe = [cloth.dict()] + wardrobe_items

        wardrobe = initialize_wardrobe(raw_wardrobe)

        fashion_rules = FashionRules()
        fashion_rules.load_conflicts("./util/conflicts.json")

        recommender = OutfitRecommenderGA(wardrobe, fashion_rules)
        target_cloth = wardrobe[0]

        top_outfits = recommender.get_top_outfits_with_item(target_cloth)

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


@app.post("/generate_outfit_with_weather")
async def generate_outfit_with_weather(request: WardrobeWeatherRequest):
    try:
        weather_desc = (
            f"Погода: {request.weather.condition}, температура {request.weather.temp}°C, "
            f"ощущается как {request.weather.feels_like}°C, влажность {request.weather.humidity}%, "
            f"облачность {request.weather.cloudness}, ветер {request.weather.wind_speed} м/с "
            f"направление {request.weather.wind_dir}."
        )
        wardrobe_items = [item.dict() for item in request.wardrobe]
        llm_answer = ask_gigachat(weather_desc, wardrobe_items)

        try:
            generated_outfits = extract_json_from_text(llm_answer)

            if not isinstance(generated_outfits, dict) or "generated_outfits" not in generated_outfits:
                raise ValueError("Invalid JSON structure")

            return {
                "status": "success",
                "generated_outfits": generated_outfits["generated_outfits"]
            }
        except Exception as e:
            print(f"Error parsing LLM response: {e}")
            print(f"Original response: {llm_answer}")
            return {
                "status": "error",
                "error": f"Failed to parse LLM response: {e}",
                "llm_raw_response": llm_answer
            }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))