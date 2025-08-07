<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Cloth_look extends Model
{
    public function cloth(){
        return $this->belongsTo('App\Cloth','id');
    }
    public function look(){
        return $this->belongsTo('App\Look','id');
    }
}
