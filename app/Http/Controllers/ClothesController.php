<?php

namespace App\Http\Controllers;

use App\Models\Cloth;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;

class ClothesController extends Controller
{
    public function baseAdd(Request $request)
    {
        $request->validate([
            'picture' => 'required|string',
        ]);

        $connectionString = 'http://127.0.0.1:8080/api/author';
        $response = Http::get($connectionString, [
            'picture' => $request->picture
        ]);
        if ($response->successful()) {
            $data = $response->json();
            return $data;
        } else {
            return $response->status();
        }
    }

    public function addAfterComp(Request $request)
    {
        $request->validate([
            "category" => "required|string",
            "base_color" => "required|string",
            "product_display_name" => "required",
            "size" => "required|string",
        ]);

        $newCloths = Cloth::create([
            'user_id' => $request->user()->id,
            ...$request->all()
        ]);
        return $newCloths;
    }


    public function get(Request $request, $id = null)
    {
        if ($id) {
            $cloth = Cloth::where('user_id', $request->user()->id)
                ->findOrFail($id);
        } else {
            $cloth = Cloth::where('user_id', $request->user()->id)->get();
        }
        return $cloth;
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

        return response(['message' => 'update success', $data], 200);
    }

    public function delete(Request $request, $id)
    {
        Cloth::where('id', $id)->where('user_id', $request->user()->id)->delete();
        return response()->noContent();
    }
}
