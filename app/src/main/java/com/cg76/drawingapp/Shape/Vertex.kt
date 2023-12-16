package com.cg76.drawingapp.Shape

class Vertex(
    var x: Float = 0f,
    var y: Float = 0f,
    var z: Float = 0f,
) {
    companion object {
        // Số lượng thành phần của mỗi đỉnh
        const val COORDS_PER_VERTEX = 3
    }

    // setter
    fun setPosition(x: Float, y: Float, z: Float){
        this.x = x;     this.y = y;    this.z = z;
    }

    fun setPosition(coords: FloatArray){
        this.x = coords[0];     this.y = coords[1];    this.z = coords[2];
    }

    // getter
     fun toFloatArray(): FloatArray {
        return floatArrayOf(x, y, z)
    }
}