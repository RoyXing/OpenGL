package com.opengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.View;

import com.opengl.shape.Triangle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author roy.xing
 * @date 2019-07-01
 */
public class GLRender implements GLSurfaceView.Renderer {

    protected View view;
    protected Triangle triangle;

    public GLRender(GLView glView) {
        view = glView;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //清空画布
        GLES20.glClearColor(0, 0, 0, 0);
        triangle = new Triangle();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        triangle.onSurfaceChanged(gl, width, height);
    }

    //不断调用
    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        triangle.onDrawFrame(gl);
    }
}
