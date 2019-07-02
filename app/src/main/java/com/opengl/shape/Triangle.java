package com.opengl.shape;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author roy.xing
 * @date 2019-07-01
 */
public class Triangle {

    //1.创建顶点数组
    //2.自己写 顶点着色器和片元着色器
    //3.将java声明的顶点数组 颜色数组 通过类似jni接口传递 给gl语言变量

    static float triangleCoords[] = {
            0.5f, 0.5f, 0f,
            -0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f
    };

    FloatBuffer vertexBuffer;

    //顶点着色器
    private String vertextShaderCode = "attribute vec4 vPosition;" +
            "uniform mat4 vMatrix;\n" +
            "void main(){" +
            "gl_Position=vMatrix*vPosition;" +
            "}";
    //片源着色器
    private final String fragmentShaderCode = "precision mediump float;" +
            "uniform  vec4 vColor;\n" +
            "void main(){\n" +
            "gl_FragColor=vColor;\t\n" +
            "}";

    int program;

    float color[] = {1.0f, 1.0f, 1.0f, 1.0f};

    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatix = new float[16];

    //初始化
    public Triangle() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        buffer.order(ByteOrder.nativeOrder());
        vertexBuffer = buffer.asFloatBuffer();
        //把这门语言推送给GPU
        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);

        //创建顶点着色器 并且在GPU进行编译
        int shader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(shader, vertextShaderCode);
        GLES20.glCompileShader(shader);

        //创建片原着色器 并且在GPU进行编译
        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader, fragmentShaderCode);
        GLES20.glCompileShader(fragmentShader);

        //将定点着色器和片原着色器统一管理
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, shader);
        GLES20.glAttachShader(program, fragmentShader);

        //链接到着色器程序
        GLES20.glLinkProgram(program);
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 120);
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7, //摄像机坐标
                0f, 0f, 0f, //目标物坐标
                0f, 1f, 1.0f);//相机方向

        Matrix.multiplyMM(mMVPMatix, 0, mProjectMatrix, 0, mViewMatrix, 0);
    }

    //渲染
    public void onDrawFrame(GL10 gl) {
        GLES20.glUseProgram(program);

        int vMatrix = GLES20.glGetUniformLocation(program, "vMatrix");
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, mMVPMatix, 0);

        //指针 native指针 gpu某个内存区域
        int mPositionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        //打开对变量读写 画点
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);

        //上色
        int mColorHandle = GLES20.glGetUniformLocation(program, "vColor");
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
