from fastapi import FastAPI, HTTPException

from services.dominant_color_algorithm import get_color

app = FastAPI()


@app.get("/")
async def root():
    return {"message": "Hello World"}


@app.get("/hello/{name}")
async def say_hello(name: str):
    return {"message": f"Hello {name}"}


@app.post("/test_color/")
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