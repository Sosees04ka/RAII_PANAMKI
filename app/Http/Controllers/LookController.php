<?php

namespace App\Http\Controllers;

use App\Models\Cloth;
use App\Models\Look;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;


class LookController extends Controller
{

    public function createLooksOnCloth(Request $request, $id)
    {
        $request->validate([
            'id' => 'integer',
        ]);

        $cloth = Cloth::where('user_id', $request->user()->id)
            ->findOrFail($id);
        $sendCloth = [
            "id" => $cloth->id,
            "masterCategory" => $cloth->master_category,
            "subCategory" => $cloth->sub_category,
            "color" => json_decode($cloth->base_color),
            "usage" => $cloth->usage,
            "imageBase64" => $cloth->picture
        ];
        $connectionString = 'http://127.0.0.1:8080/generate_outfit';
        $response = Http::post($connectionString, [
            'cloth' => $sendCloth,
            'wardrobe' => $this->getAllUserCloth($request)
        ]);
        if ($response->successful()) {
            $data = $response->json();
            //$response = json_decode($incomingJson, true); // превращаем в массив

            $cloth = [
                'id' => $data['main_item']['id'],
                'master_category' => $data['main_item']['masterCategory'],
                'sub_category' => $data['main_item']['subCategory'],
                'base_color' => json_encode($data['main_item']['color']), // в JSON для БД
                'usage' => $data['main_item']['usage'],
                'picture' => $data['main_item']['imageBase64'],
            ];

            $generatedOutfits = collect($data['generated_outfits'])->map(function ($outfit) {
                return [
                    'score' => $outfit['score'],
                    'items' => collect($outfit['items'])->map(function ($item) {
                        return [
                            'id' => $item['id'],
                            'master_category' => $item['masterCategory'],
                            'sub_category' => $item['subCategory'],
                            'base_color' => json_encode($item['color']),
                            'usage' => $item['usage'],
                            'picture' => $item['imageBase64'],
                        ];
                    })->toArray(),
                ];
            })->toArray();

            return [
                'cloth' => $cloth,
                'generated_outfits' => $generatedOutfits
            ];
        } else {
            return $response->status();
        }
    }

    public function createLookRandom(Request $request)
    {
        $connectionString = 'http://127.0.0.1:8080/api/author';
        $response = Http::get($connectionString, [
            'wardrobe' => $this->getAllUserCloth($request)
        ]);
        if ($response->successful()) {
            $data = $response->json();
            $generatedOutfits = collect($data['generated_outfits'])->map(function ($outfit) {
                return [
                    'score' => $outfit['score'],
                    'items' => collect($outfit['items'])->map(function ($item) {
                        return [
                            'id' => $item['id'],
                            'master_category' => $item['masterCategory'],
                            'sub_category' => $item['subCategory'],
                            'base_color' => json_encode($item['color']),
                            'usage' => $item['usage'],
                            'picture' => $item['imageBase64'],
                        ];
                    })->toArray(),
                ];
            })->toArray();

            return [
                'generated_outfits' => $generatedOutfits
            ];
        } else {
            return $response->status();
        }
    }

    public function createLookOnPicture(Request $request)
    {
        $connectionString = 'http://127.0.0.1:8080/generate_outfit_with_base64';
        $response = Http::post($connectionString, [
            'imageBase64' => $request->get('picture'),
            'wardrobe' => $this->getAllUserCloth($request)
        ]);
        if ($response->successful()) {
            $data = $response->json();

            $cloth = [
                'id' => $data['main_item']['id'],
                'master_category' => $data['main_item']['masterCategory'],
                'sub_category' => $data['main_item']['subCategory'],
                'base_color' => json_encode($data['main_item']['color']),
                'usage' => $data['main_item']['usage'],
                'picture' => $data['main_item']['imageBase64'],
            ];

            $generatedOutfits = collect($data['generated_outfits'])->map(function ($outfit) {
                return [
                    'score' => $outfit['score'],
                    'items' => collect($outfit['items'])->map(function ($item) {
                        return [
                            'id' => $item['id'],
                            'master_category' => $item['masterCategory'],
                            'sub_category' => $item['subCategory'],
                            'base_color' => json_encode($item['color']),
                            'usage' => $item['usage'],
                            'picture' => $item['imageBase64'],
                        ];
                    })->toArray(),
                ];
            })->toArray();

            return [
                'cloth' => $cloth,
                'generated_outfits' => $generatedOutfits
            ];
        } else {
            return $response->status();
        }
    }

    public function createLooksOnWeather(Request $request)
    {
        $data = $request->validate([
            'lat' => 'string|required',
            'lon' => 'string|required',
        ]);

        $access_key = env("YANDEX_KEY");

        $opts = array(
            'http' => array(
                'method' => 'GET',
                'header' => 'X-Yandex-Weather-Key: ' . $access_key
            )
        );

        $context = stream_context_create($opts);

        $file =
            file_get_contents('https://api.weather.yandex.ru/v2/forecast?lat=' . $data['lat'] . '&lon=' . $data['lon'],
                false, $context);
        dd(json_decode($file));

        //метод для образа
        $connectionString = 'http://127.0.0.1:8080';
        $response = Http::get($connectionString, [
            'weather' => $file,
            'wardrobe' => $this->getAllUserCloth($request)
        ]);
        if ($response->successful()) {
            $data = $response->json();
            $generatedOutfits = collect($data['generated_outfits'])->map(function ($outfit) {
                return [
                    'score' => $outfit['score'],
                    'items' => collect($outfit['items'])->map(function ($item) {
                        return [
                            'id' => $item['id'],
                            'master_category' => $item['masterCategory'],
                            'sub_category' => $item['subCategory'],
                            'base_color' => json_encode($item['color']),
                            'usage' => $item['usage'],
                            'picture' => $item['imageBase64'],
                        ];
                    })->toArray(),
                ];
            })->toArray();

            return [
                'generated_outfits' => $generatedOutfits
            ];
        } else {
            return $response->status();
        }
    }

    public function store(Request $request)
    {
        $request->validate([
            'cloth' => 'array|required',
            'outfit' => 'array|required',
            'description' => 'text',
        ]);

        $newLooks = Look::create([
            'user_id' => $request->user()->id,
            'description' => $request->description,
        ]);
        return response(null, 204);
    }

    public function show(Request $request, $id)
    {
        if ($id) {
            $look = Look::with('cloth_look.cloth')->findOrFail($id);
        } else {
            $look = Look::where('user_id', $request->user()->id)
                ->with('cloth_look.cloth')
                ->get();
        }
        return json_encode($look);
    }

    public function destroy(string $id)
    {
        Look::destroy($id);
        return response(null, 204);
    }

    public function getAllUserCloth(Request $request)
    {
        return Cloth::select(
            'id',
            'base_color',
            'master_category',
            'sub_category',
            'usage',
            'picture'
        )
            ->where('user_id', $request->user()->id)
            ->get()
            ->map(function ($cloth) {
                return [
                    'id' => $cloth->id,
                    'masterCategory' => $cloth->master_category,
                    'subCategory' => $cloth->sub_category,
                    'color' => json_decode($cloth->base_color),
                    'usage' => $cloth->usage,
                    'imageBase64' => $cloth->picture,
                ];
            });
    }
}
