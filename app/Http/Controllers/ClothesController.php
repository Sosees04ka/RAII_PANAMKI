<?php

namespace App\Http\Controllers;

use App\Enums\ClothCategory;
use App\Models\Cloth;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;

class ClothesController extends Controller
{
    public function add(Request $request)
    {
        $request->validate([
            'picture' => 'required|string',
            "product_display_name" => "required|string",
        ]);
        set_time_limit(0);
        $connectionString = 'http://127.0.0.1:8080/add_cloth_to_wardrobe';
        $response = Http::withOptions([
            'timeout' => 720,
            'connect_timeout' => 60,
        ])->post($connectionString, [
            'img64' => $request->picture
        ]);
        if ($response->successful()) {
            $data = $response->json();
            $newCloths = Cloth::create([
                'user_id' => $request->user()->id,
                'product_display_name' => $request->product_display_name,
                'picture' => $request->picture,
                'base_color' => $data['color'],
                'usage' => $data['usage'],
                'master_category' => $data['masterCategory'],
                'sub_category' => $data['subCategory'],
            ]);
            return ['clothId' => $newCloths->id];
        } else {
            return $response->status();
        }
    }

    public function get(Request $request, $id = null)
    {
        if ($id) {
            $cloth = Cloth::where('user_id', $request->user()->id)->where('status', true)
                ->findOrFail($id);
            $cloth->master_category = ClothCategory::from($cloth->master_category)->label();
            $cloth->sub_category = ClothCategory::from($cloth->sub_category)->label();
            $cloth->usage = ClothCategory::from($cloth->usage)->label();
        } else {
            $cloth = Cloth::where('user_id', $request->user()->id)->get();
        }
        return json_encode($cloth);
    }

    public function getPreview(Request $request)
    {
        return json_encode(Cloth::select('id', 'picture', 'product_display_name')
            ->where('user_id', $request->user()->id)
            ->get());
    }

    public function update(Request $request, $id)
    {
        $data = $request->only([
            'size',
            'product_display_name'
        ]);
        $data = array_filter($data, function ($value) {
            return $value !== null;
        });

        $updated = Cloth::where('user_id', $request->user()->id)->where('id', $id)->update($data);

        return response(['message' => 'update success', $updated], 200);
    }

    public function delete(Request $request, $id)
    {
        Cloth::where('id', $id)->where('user_id', $request->user()->id)->delete();
        return response(null, 204);
    }
}
