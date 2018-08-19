package com.onething.panoramaopengltutoria;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer {
    private static String TAG = "GLRenderer";
    private Context context;
    private int programId;
    private int aPositionHandle;
    private FloatBuffer vertexBuffer;
    private final float[] vertexData = {
            0f, 0f, 0f,
            1f, -1f, 0f,
            1f, 1f, 0f,
    };
    private final float[] projectionMatrix = new float[16];
    private int uMatrixHandle;

    public GLRenderer(Context ctx) {
        context = ctx;

        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated lance");
        String vertex_shader = ShaderUtils.readRawTextFile(context, R.raw.vertex_shader);
        String fragment_shader = ShaderUtils.readRawTextFile(context, R.raw.fragment_shader);
        programId = ShaderUtils.createProgram(vertex_shader, fragment_shader);
        aPositionHandle = GLES20.glGetAttribLocation(programId, "aPosition");
        uMatrixHandle = GLES20.glGetUniformLocation(programId, "uMatrix");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged lance");
        float ratio = width > height ?
                (float)width / height :
                (float)height / width;
        if(width > height) {
            Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1, 1, -1, 1);
        } else {
            Matrix.orthoM(projectionMatrix, 0, -1, 1, -ratio, ratio, -1, 1);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(programId);
        GLES20.glUniformMatrix4fv(uMatrixHandle, 1, false, projectionMatrix, 0);
        GLES20.glEnableVertexAttribArray(aPositionHandle);
        GLES20.glVertexAttribPointer(aPositionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }
}
