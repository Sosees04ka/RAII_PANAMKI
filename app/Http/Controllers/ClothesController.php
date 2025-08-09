<?php

namespace App\Http\Controllers;

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

        $newCloths = Cloth::create([
            'user_id' => $request->user()->id,
            'picture' => $request->picture,
            'product_display_name' => $request->product_display_name,
            'base_color' => [0, 0, 0],
            'usage' => "m",
            'master_category' => "m",
            'sub_category' => "m",
        ]);

        return ['clothId' => $newCloths->id];

        $connectionString = 'http://127.0.0.1:8080/api/author';
        $response = Http::get($connectionString, [
            'picture' => $request->picture
        ]);
        if ($response->successful()) {
            $data = $response->json();
            $newCloths = Cloth::create([
                'user_id' => $request->user()->id,
                'product_display_name' => $request->product_display_name,
                'picture' => $request->picture,
                'base_color' => $data['color'],
                'usage' => $data['usage'],
                'master_category' => $data['master_category'],
                'sub_category' => $data['sub_category'],
            ]);
            return ['clothId' => $newCloths->id];
        } else {
            return $response->status();
        }
    }

    public function get(Request $request, $id = null)
    {
        if ($id) {
            $cloth = Cloth::where('user_id', $request->user()->id)
                ->findOrFail($id);
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
