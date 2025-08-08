<?php

namespace App\Http\Controllers;

use App\Models\Cloth;
use Illuminate\Http\Request;

class ClothesController
{
    public function add(Request $request)
    {
        $request->validate([
            "gender" => "required",
            "sub_category" => "required",
            "master_category" => "required",
            "article_category" => "required",
            "base_color" => "required",
            "season" => "required",
            "usage" => "required",
            "product_display_name" => "required",
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
            'gender',
            'sub_category',
            'master_category',
            'article_category',
            'base_color',
            'season',
            'usage',
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
