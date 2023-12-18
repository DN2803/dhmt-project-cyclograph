package com.cg76.drawingapp.Shape

import android.util.Log

class BuilderFactory {
    private val _prototypes = mutableMapOf<ShapeType, ShapeBuilder>()

    fun registerWith(type: ShapeType, builder: ShapeBuilder){
        _prototypes[type] = builder
    }

    fun select(type: ShapeType): ShapeBuilder?{
        var builder: ShapeBuilder? = null

        if (_prototypes.containsKey(type)){
            builder = _prototypes[type]!!
        }
        else {
            Log.e("Build Error", "Unregisted Type")
        }

        return builder
    }
}