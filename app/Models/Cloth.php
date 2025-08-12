<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Cloth extends Model
{
    public $timestamps = false;
    protected $fillable = [
        'user_id',
        'master_category',
        'sub_category',
        'base_color',
        'usage',
        'product_display_name',
        'picture',
        'status'
    ];

    protected $casts = [
        'base_color' => 'array',
    ];

    public function looks()
    {
        return $this->belongsToMany(Look::class, 'cloths_look', 'cloth_id', 'look_id');
    }

    public function user()
    {
        return $this->belongsTo('App\User', 'user_id');
    }
}
