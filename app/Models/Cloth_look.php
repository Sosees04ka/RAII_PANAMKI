<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Cloth_look extends Model
{
    protected $table = 'cloths_look';
    public $incrementing = false;
    public $timestamps = true;

    protected $fillable = ['cloth_id', 'look_id'];

    public function cloth()
    {
        return $this->belongsTo(Cloth::class, 'cloth_id', 'id');
    }

    public function look()
    {
        return $this->belongsTo(Look::class, 'look_id', 'id');
    }
}
