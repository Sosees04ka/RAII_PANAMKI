<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Cloth extends Model
{
    public $timestamps = false;
    protected $fillable = [
        'user_id',
        'gender',
        'sub_category',
        'master_category',
        'article_category',
        'base_color',
        'season',
        'usage',
        'product_display_name',
    ];

    public function user()
    {
        return $this->belongsTo('App\User', 'user_id');
    }
}
