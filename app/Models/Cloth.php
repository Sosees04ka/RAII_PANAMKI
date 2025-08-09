<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Cloth extends Model
{
    public $timestamps = false;
    protected $fillable = [
        'user_id',
        'category',
        'base_color',
        'product_display_name',
        'size',
        'picture'
    ];

    public function user()
    {
        return $this->belongsTo('App\User', 'user_id');
    }
}
