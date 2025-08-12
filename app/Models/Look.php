<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Look extends Model
{
     protected $fillable = [
         'user_id',
         'score',
        'like_status',
        'description',
    ];

    public function cloth()
    {
        return $this->belongsToMany(Cloth::class, 'cloths_look', 'look_id', 'cloth_id');
    }

    public function user(){
        return $this->belongsTo('App\User','user_id');
    }

}
