package com.cg76.drawingapp.Shape

class Vertex(
    private var _x: Float = 0f,
    private var _y: Float = 0f,
    private var _z: Float = 0f,
) {
    companion object {
        // Số lượng thành phần của mỗi đỉnh
        const val COORDS_PER_VERTEX = 3
    }

    var x: Float
        get() = _x
        set(x) {_x=x}

    var y: Float
        get() = _y
        set(y) {_y=y}

     fun toFloatArray(): FloatArray {
        return floatArrayOf(_x, _y, _z)
    }
}