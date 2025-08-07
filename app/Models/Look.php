<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Look extends Model
{
     protected $fillable = [
        'like_status',
        'description',
    ];

    public function user(){
        return $this->belongsTo('App\User','user_id');
    }

}
