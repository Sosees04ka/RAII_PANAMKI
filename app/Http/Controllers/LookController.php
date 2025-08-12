<?php

namespace App\Http\Controllers;

use App\Enums\ClothCategory;
use App\Models\Cloth;
use App\Models\Cloth_look;
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
        Look::where('like_status', false)->delete();
        $cloth = Cloth::where('user_id', $request->user()->id)
            ->findOrFail($id);
        $sendCloth = [
            "id" => $cloth->id,
            "masterCategory" => $cloth->master_category,
            "subCategory" => $cloth->sub_category,
            "color" => $cloth->base_color,
            "usage" => $cloth->usage,
            "imageBase64" => $cloth->picture
        ];
        set_time_limit(0);
        ini_set('max_execution_time', 0);
        ini_set('default_socket_timeout', 720);
        $connectionString = 'http://127.0.0.1:8080/generate_outfit_ga';
        $response = Http::withOptions([
            'timeout' => 720,
            'connect_timeout' => 60,
        ])->timeout(720)->connectTimeout(300)->post($connectionString, [
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
                'base_color' => $data['main_item']['color'], // в JSON для БД
                'usage' => $data['main_item']['usage'],
                'picture' => $data['main_item']['imageBase64'],
            ];

            $outfits = $this->lookCheck($data['generated_outfits'], $request);

            return
                $outfits;
        } else {
            return $response->status();
        }
    }

//    public function createLookRandom(Request $request)
//    {
//        $connectionString = 'http://127.0.0.1:8080/api/author';
//        $response = Http::get($connectionString, [
//            'wardrobe' => $this->getAllUserCloth($request)
//        ]);
//        if ($response->successful()) {
//            $data = $response->json();
//            $generatedOutfits = collect($data['generated_outfits'])->map(function ($outfit) {
//                return [
//                    'score' => $outfit['score'],
//                    'items' => collect($outfit['items'])->map(function ($item) {
//                        return [
//                            'id' => $item['id'],
//                            'product_display_name' => Cloth::where('id', $item['id'])->first()->product_display_name,
//                            'master_category' => $item['masterCategory'],
//                            'sub_category' => $item['subCategory'],
//                            'base_color' => json_encode($item['color']),
//                            'usage' => $item['usage'],
//                            'picture' => $item['imageBase64'],
//                        ];
//                    })->toArray(),
//                ];
//            })->toArray();
//
//            return [
//                'generated_outfits' => $generatedOutfits
//            ];
//        } else {
//            return $response->status();
//        }
//    }

    public function createLookOnPicture(Request $request)
    {
        $request->validate([
            'picture' => 'required|string',
            "product_display_name" => "required|string",
        ]);
        Look::where('like_status', false)->delete();
        set_time_limit(0);
        ini_set('max_execution_time', 0);
        ini_set('default_socket_timeout', 720);
        $connectionString = 'http://127.0.0.1:8080/generate_outfit_with_base64_ga';
        $response = Http::withOptions([
            'timeout' => 720,
            'connect_timeout' => 60,
        ])->post($connectionString, [
            'imageBase64' => $request->picture,
            'wardrobe' => $this->getAllUserCloth($request)
        ]);
        if ($response->successful()) {
            $data = $response->json();

            $cloth = Cloth::create([
                'user_id' => $request->user()->id,
                'product_display_name' => $request->product_display_name,
                'master_category' => $data['main_item']['masterCategory'],
                'sub_category' => $data['main_item']['subCategory'],
                'base_color' => $data['main_item']['color'],
                'usage' => $data['main_item']['usage'],
                'picture' => $request->picture,
            ]);

            $outfits = $this->lookCheck($data['generated_outfits'], $request, $cloth);

            return $outfits;
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
        //метод для образа
        Look::where('like_status', false)->delete();
        set_time_limit(0);
        ini_set('max_execution_time', 0);
        ini_set('default_socket_timeout', 720);
        $connectionString = 'http://127.0.0.1:8080/generate_outfit_with_weather';
        $response = Http::withOptions([
            'timeout' => 720,
            'connect_timeout' => 60,
        ])->timeout(720)->connectTimeout(300)->post($connectionString, [
            'wardrobe' => $this->getAllUserCloth($request),
            'weather' => json_decode($file)->fact
        ]);
        if ($response->successful()) {
            $data = $response->json();
            $outfits = $this->lookCheck($data['generated_outfits'], $request);

            foreach ($outfits as &$outfit) {  // & для ссылки на элемент
                foreach ($outfit['items'] as &$item) {  // & для ссылки на элемент
                    if (isset($item['id'])) {
                        $cloth = Cloth::find($item['id']);
                        $item['picture'] = $cloth ? $cloth->picture : null;
                    }
                }
            }
            unset($outfit, $item);

            return $outfits;
        } else {
            return $response->status();
        }
    }

    public function store(Request $request, $id)
    {
        Look::where('user_id', $request->user()->id)->where('id', $id)->first()->update([
            'like_status' => true
        ]);
        $look = Look::with('cloth')->first();

        if ($look) {
            $clothFalse = $look->cloth->first(fn($cloth) => $cloth->status === false);

            if ($clothFalse) {
                $idFalseCloth = $clothFalse->id;
                Cloth::where('id', $idFalseCloth)->update([
                    'status'=>true
                ]);
            }
        }
        return response(null, 200);
    }

    public function show(Request $request, $id = null)
    {
        if ($id) {
            $look = Look::with('cloth')->findOrFail($id);

            foreach ($look->cloth as $cloth) {
                $cloth->master_category = ClothCategory::from($cloth->master_category)->label();
                $cloth->sub_category = ClothCategory::from($cloth->sub_category)->label();
                $cloth->usage = ClothCategory::from($cloth->usage)->label();
            }

            return [
                'id' => $look->id,
                'score' => $look->score,
                'like_status' => $look->like_status,
                'description' => $look->description,
                'items' => $look->cloth,
            ];
        } else {
            $looks = Look::where('user_id', $request->user()->id)
                ->where('like_status', true)
                ->with('cloth')
                ->get();

            // Возвращаем массив образов, каждый с нужной структурой
            return $looks->map(function($look) {
                foreach ($look->cloth as $cloth) {
                    $cloth->master_category = ClothCategory::from($cloth->master_category)->label();
                    $cloth->sub_category = ClothCategory::from($cloth->sub_category)->label();
                    $cloth->usage = ClothCategory::from($cloth->usage)->label();
                }
                return [
                    'id' => $look->id,
                    'score' => $look->score,
                    'like_status' => $look->like_status,
                    'description' => $look->description,
                    'items' => $look->cloth,
                ];
            });
        }
    }

    public function destroy(Request $request, $id)
    {
        Look::where('user_id', $request->user()->id)->where('id', $id)->first()->update([
            'like_status' => false
        ]);
        return response(null, 200);
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
                    'color' => $cloth->base_color,
                    'usage' => $cloth->usage,
                    'imageBase64' => $cloth->picture,
                ];
            });
    }

    public function lookCheck($generatedOutfits, Request $request, $cloth = null): array
    {
        $outfits = array();
        foreach ($generatedOutfits as $outfit) {
//            $itemIds = collect($outfit['items'])->pluck('id')->sort()->values()->toArray();
//
//            $existingLook = Look::where('user_id', $request->user()->id)
//                ->whereHas('cloth', function ($query) use ($itemIds) {
//                    $query->whereIn('cloth_id', $itemIds);
//                }, '=', count($itemIds)) // связей с этими cloth_id должно быть ровно count($itemIds)
//                ->get()
//                ->first(function ($look) use ($itemIds) {
//                    // Проверяем, что у этого лука именно столько же связанных cloth, сколько в $itemIds
//                    return $look->cloth()->count() === count($itemIds);
//                });
            $existingLook = false;
            if ($existingLook) {
                $outfits[] = $this->getOutfits($existingLook->id, $outfit, $existingLook->like_status);
            } else {
                $look = Look::create([
                    'like_status' => false,
                    'score' => $outfit['score'],
                    'description' => $outfit['description'] ?? "",
                    'user_id' => $request->user()->id,
                ]);

                foreach ($outfit['items'] as $item) {
                    if($item['id'] == null and $cloth != null) {
                        $item = $cloth;
                        Cloth_look::create([
                            'cloth_id' => $cloth->id,
                            'look_id' => $look->id,
                        ]);
                        unset($item);
                    }
                    else{
                        Cloth_look::create([
                            'cloth_id' => $item['id'],
                            'look_id' => $look->id,
                        ]);
                    }
                }
                $outfits[] = $this->getOutfits($look->id, $outfit, false);
            }
        }
        return $outfits;
    }

    public function getOutfits($id, $outfit, $status): array
    {
        return [
            'id' => $id,
            'score' => $outfit['score'],
            'like_status' => $status,
            'description' => "",
            'items' => collect($outfit['items'])->map(function ($item) {
                return [
                    'id' => $item['id'],
                    'master_category' => ClothCategory::from($item['masterCategory'])->label(),
                    'product_display_name' => Cloth::where('id', $item['id'])->value('product_display_name') ?? 'временный продукт',
                    'sub_category' => ClothCategory::from($item['subCategory'])->label(),
                    'base_color' => $item['color'],
                    'usage' => $item['usage'],
                    'picture' => $item['imageBase64'],
                ];
            })->toArray(),
        ];
    }
}
