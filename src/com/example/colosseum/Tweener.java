package com.example.colosseum;


/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.animation.Animator.AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.util.Log;
import  android.animation.ValueAnimator;
import android.animation.Keyframe;
class Tweener {
    private static final String TAG = "Tweener";
    private static final boolean DEBUG = false;

    ObjectAnimator animator;
    private static HashMap<Object, Tweener> sTweens = new HashMap<Object, Tweener>();

    public Tweener(ObjectAnimator anim) {
        animator = anim;
    }

    private static void remove(Animator animator) {
        Iterator<Entry<Object, Tweener>> iter = sTweens.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Object, Tweener> entry = iter.next();
            if (entry.getValue().animator == animator) {
                if (DEBUG) Log.v(TAG, "Removing tweener " + sTweens.get(entry.getKey())
                        + " sTweens.size() = " + sTweens.size());
                iter.remove();
                break; // an animator can only be attached to one object
            }
        }
    }

    public static Tweener to(Object object, long duration, Object... vars) {
        long delay = 0;
        AnimatorUpdateListener updateListener = null;
        AnimatorListener listener = null;
        TimeInterpolator interpolator = null;

        // Iterate through arguments and discover properties to animate
        ArrayList<PropertyValuesHolder> props = new ArrayList<PropertyValuesHolder>(vars.length/2);
        for (int i = 0; i < vars.length; i+=2) {
            if (!(vars[i] instanceof String)) {
                throw new IllegalArgumentException("Key must be a string: " + vars[i]);
            }
            String key = (String) vars[i];
            Object value = vars[i+1];
			Log.d("TEST","key="+key+" value="+value);
            if ("simultaneousTween".equals(key)) {
                // TODO
            } else if ("ease".equals(key)) {
                interpolator = (TimeInterpolator) value; // TODO: multiple interpolators?
            } else if ("onUpdate".equals(key) || "onUpdateListener".equals(key)) {
                updateListener = (AnimatorUpdateListener) value;
            } else if ("onComplete".equals(key) || "onCompleteListener".equals(key)) {
                listener = (AnimatorListener) value;
            } else if ("delay".equals(key)) {
                delay = ((Number) value).longValue();
            } else if ("syncWith".equals(key)) {
                // TODO
            } else if (value instanceof float[]) {
                props.add(PropertyValuesHolder.ofFloat(key,
                        ((float[])value)[0], ((float[])value)[1]));
				Log.d("TEST","1="+((float[])value)[0]+"  2="+((float[])value)[1]);
            } else if (value instanceof int[]) {
                props.add(PropertyValuesHolder.ofInt(key,
                        ((int[])value)[0], ((int[])value)[1]));
						Log.d("TEST=====berrytao","1="+((int[])value)[0]+"  2="+((int[])value)[1]);
            } else if (value instanceof Number) {
                float floatValue = ((Number)value).floatValue();
                props.add(PropertyValuesHolder.ofFloat(key, floatValue));
            } else {
                throw new IllegalArgumentException(
                        "Bad argument for key \"" + key + "\" with value " + value.getClass());
            }
        }

        // Re-use existing tween, if present
        Tweener tween = sTweens.get(object);
        ObjectAnimator anim = null;
        if (tween == null) {
            anim = ObjectAnimator.ofPropertyValuesHolder(object,
                    props.toArray(new PropertyValuesHolder[props.size()]));
            tween = new Tweener(anim);
            sTweens.put(object, tween);
            if (DEBUG) Log.v(TAG, "Added new Tweener " + tween);
        } else {
            anim = sTweens.get(object).animator;
            replace(props, object); // Cancel all animators for given object
        }

        if (interpolator != null) {
            anim.setInterpolator(interpolator);
        }

        // Update animation with properties discovered in loop above
        anim.setStartDelay(delay);
        anim.setDuration(duration);
        if (updateListener != null) {
            anim.removeAllUpdateListeners(); // There should be only one
            anim.addUpdateListener(updateListener);
        }
        if (listener != null) {
            anim.removeAllListeners(); // There should be only one.
            anim.addListener(listener);
        }
        anim.addListener(mCleanupListener);
		anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setRepeatMode(ValueAnimator.REVERSE);

        return tween;
    }

    Tweener from(Object object, long duration, Object... vars) {
        // TODO:  for v of vars
        //            toVars[v] = object[v]
        //            object[v] = vars[v]
        return Tweener.to(object, duration, vars);
    }

    // Listener to watch for completed animations and remove them.
    private static AnimatorListener mCleanupListener = new AnimatorListenerAdapter() {

        @Override
        public void onAnimationEnd(Animator animation) {
            remove(animation);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            remove(animation);
        }
    };

    public static void reset() {
        if (DEBUG) {
            Log.v(TAG, "Reset()");
            if (sTweens.size() > 0) {
                Log.v(TAG, "Cleaning up " + sTweens.size() + " animations");
            }
        }
        sTweens.clear();
    }

    private static void replace(ArrayList<PropertyValuesHolder> props, Object... args) {
        for (final Object killobject : args) {
            Tweener tween = sTweens.get(killobject);
            if (tween != null) {
                tween.animator.cancel();
                if (props != null) {
                    tween.animator.setValues(
                            props.toArray(new PropertyValuesHolder[props.size()]));
                } else {
                    sTweens.remove(tween);
                }
            }
        }
    }
	
	 
    //add by berrytao
    public static Tweener alphaAnim3(Object object,long startDelay,long duration,float alp1,float alp2,float alp3){
    	//前半段透明度从alp1变到alp2，后半段从alp2度变到alp3度  
    	Keyframe kf0 = Keyframe.ofFloat(0f, alp1);  
    	Keyframe kf1 = Keyframe.ofFloat(.5f, alp2);  
    	Keyframe kf2 = Keyframe.ofFloat(1f, alp3);  		
    	PropertyValuesHolder alpRotation = PropertyValuesHolder.ofKeyframe("alpha", kf0, kf1, kf2);  
		Tweener tween = sTweens.get(object);
    	ObjectAnimator alpAnimator = ObjectAnimator.ofPropertyValuesHolder(object, alpRotation) ; 
		tween = new Tweener(alpAnimator);
		 if (!sTweens.containsKey(object)) {
         	 sTweens.put(object, tween);
         	if (DEBUG) Log.v(TAG, "Added new Tweener " + tween);
		}
		alpAnimator.setStartDelay(startDelay);
    	alpAnimator.setDuration(duration);  
		alpAnimator.setRepeatCount(0);//ValueAnimator.INFINITE
        alpAnimator.setRepeatMode(ValueAnimator.REVERSE);//REVERSE
		return new Tweener(alpAnimator);
    }
	
	  //add by berrytao 第一个，第三个箭头动画
    public static Tweener alphaAnim4(Object object,long startDelay,long duration,float alp0,float alp1,float alp2,float alp3){
    	Keyframe kf0 = Keyframe.ofFloat(0f, alp0);  
    	Keyframe kf1 = Keyframe.ofFloat(.33f, alp1);  
    	Keyframe kf2 = Keyframe.ofFloat(0.66f, alp2);  	
    	Keyframe kf3 = Keyframe.ofFloat(1f, alp3);  	  		
    	PropertyValuesHolder alpRotation = PropertyValuesHolder.ofKeyframe("alpha", kf0, kf1, kf2,kf3);  
		Tweener tween = sTweens.get(object);
    	ObjectAnimator alpAnimator = ObjectAnimator.ofPropertyValuesHolder(object, alpRotation) ; 
		tween = new Tweener(alpAnimator);
		 if (!sTweens.containsKey(object)) {
         	 sTweens.put(object, tween);
         	if (DEBUG) Log.v(TAG, "Added new Tweener " + tween);
		}
		alpAnimator.setStartDelay(startDelay);
    	alpAnimator.setDuration(duration);  
		alpAnimator.setRepeatCount(ValueAnimator.INFINITE);
        alpAnimator.setRepeatMode(ValueAnimator.RESTART);
		return new Tweener(alpAnimator);
    }
	
	//add by berrytao 第二个箭头动画
	 public static Tweener alphaAnim5(Object object,long startDelay,long duration,float alp0,float alp1,float alp2,float alp3,float alp4){  
    	Keyframe kf0 = Keyframe.ofFloat(0f, alp0);  
    	Keyframe kf1 = Keyframe.ofFloat(.17f, alp1);  
    	Keyframe kf2 = Keyframe.ofFloat(0.5f, alp2);  
    	Keyframe kf3 = Keyframe.ofFloat(0.83f, alp3);  
    	Keyframe kf4 = Keyframe.ofFloat(1f, alp4);  
    	
    	PropertyValuesHolder alpRotation = PropertyValuesHolder.ofKeyframe("alpha", kf0, kf1, kf2,kf3,kf4);  
		Tweener tween = sTweens.get(object);
    	ObjectAnimator alpAnimator = ObjectAnimator.ofPropertyValuesHolder(object, alpRotation) ; 
		tween = new Tweener(alpAnimator);
		 if (!sTweens.containsKey(object)) {
         	 sTweens.put(object, tween);
         	if (DEBUG) Log.v(TAG, "Added new Tweener " + tween);
		}
		alpAnimator.setStartDelay(startDelay);
    	alpAnimator.setDuration(duration);  
		alpAnimator.setRepeatCount(ValueAnimator.INFINITE);
        alpAnimator.setRepeatMode(ValueAnimator.RESTART);
		return new Tweener(alpAnimator);
    }	
	 public static Tweener rotateAnim(Object object,float fromDegrees,float toDegrees){
    	//前半段透明度从100%变到20%，后半段从20%度变到50%度  
    	Keyframe fromKf = Keyframe.ofFloat(0f, fromDegrees);   
    	Keyframe toKf = Keyframe.ofFloat(1f, toDegrees);  
    	PropertyValuesHolder alpRotation = PropertyValuesHolder.ofKeyframe("rotation", fromKf, toKf);  
		Tweener tween = sTweens.get(object);
    	//ObjectAnimator alpAnimator = ObjectAnimator.ofPropertyValuesHolder(object, alpRotation) ; 
		ObjectAnimator alpAnimator = ObjectAnimator.ofFloat(object,"rotationY", fromDegrees,toDegrees) ; 
		tween = new Tweener(alpAnimator);
		 if (!sTweens.containsKey(object)) {
         	 sTweens.put(object, tween);
         	if (DEBUG) Log.v(TAG, "Added new Tweener " + tween);
		}
    	alpAnimator.setDuration(1000);  
		//alpAnimator.setRepeatCount(ValueAnimator.INFINITE);
      //  alpAnimator.setRepeatMode(ValueAnimator.RESTART);
		return new Tweener(alpAnimator);
    }
}
