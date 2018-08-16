package com.kingpei.viewmodel.layoutManager

import java.security.InvalidParameterException

class ArcCalculator{

    constructor(radius:Int, width:Int){
        this.radius = radius
        this.halfWidth   = width/2
        this.uselessPart = Math.sqrt((radius * radius - halfWidth * halfWidth).toDouble()).toInt()

        if(!checkArc()){
            throw InvalidParameterException("radius should be bigger than half width")
        }
    }

     var radius:Int = 0
        set(value)  {
            field = value
            this.uselessPart = Math.sqrt((field * field - halfWidth * halfWidth).toDouble()).toInt()
        }

    var halfWidth:Int = 0
    set(value)  {
        field = value
        this.uselessPart = Math.sqrt((radius * radius - field * field).toDouble()).toInt()
    }

     private var uselessPart:Int = 0

    public fun getTopForItem(left:Int, right:Int) : Int{
        var tempLeft = Math.abs(left - halfWidth)
        var leftY = Math.sqrt((radius * radius - tempLeft * tempLeft).toDouble()).toInt()
        var tempRight = Math.abs(right - halfWidth)
        var rightY = Math.sqrt((radius * radius - tempRight * tempRight).toDouble()).toInt()

        if(tempLeft > halfWidth && tempRight > halfWidth){
            return 0
        }

        return Math.max(leftY, rightY) - uselessPart
    }

    public fun getUsedHeight():Int{
        return radius - uselessPart
    }

    public fun checkArc():Boolean{
        return radius > halfWidth
    }
}
