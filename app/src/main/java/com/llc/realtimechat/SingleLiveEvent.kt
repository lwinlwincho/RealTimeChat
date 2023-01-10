package com.llc.realtimechat

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

class SingleLiveEvent<T> : MutableLiveData<T>() {
    //Thread A { a=2 result = a+2}
    //Thread B {a=3]
    //AtomicBoolean avoid to get two results between Thread A running and set value to B
   // private var isPending=false
    private var isPending:AtomicBoolean = AtomicBoolean(false)

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner) { value ->
            //you set data but you did not still observe data, set original observer to data
          /* compareAndSet means
           if(isPending==true){
               observer.onChanged(value)
               isPending=false
           } */

            if (isPending.compareAndSet(true,false)) {
                observer.onChanged(value)
            }
        }
    }

    override fun setValue(value: T) {
        isPending.set(true)
        super.setValue(value)
    }
}