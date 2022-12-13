package com.mrzak34.thunderhack.util.shaders;

import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Shader {
    protected int program;
    protected Map<String, Integer> uniformsMap;

    public Shader(final String fragmentShader) {
        int vertexShaderID;
        int fragmentShaderID;
        try {
            final InputStream vertexStream = this.getClass().getResourceAsStream("/assets/gamesense/shaders/vertex.vert");
            vertexShaderID = this.createShader(IOUtils.toString(vertexStream), 35633);
            IOUtils.closeQuietly(vertexStream);
            final InputStream fragmentStream = this.getClass().getResourceAsStream("/assets/gamesense/fragment/" + fragmentShader);
            fragmentShaderID = this.createShader( IOUtils.toString(fragmentStream), 35632);
            IOUtils.closeQuietly(fragmentStream);
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (vertexShaderID == 0 || fragmentShaderID == 0) {
            return;
        }
        this.program = ARBShaderObjects.glCreateProgramObjectARB();
        if (this.program == 0) {
            return;
        }
        ARBShaderObjects.glAttachObjectARB(this.program, vertexShaderID);
        ARBShaderObjects.glAttachObjectARB(this.program, fragmentShaderID);
        ARBShaderObjects.glLinkProgramARB(this.program);
        ARBShaderObjects.glValidateProgramARB(this.program);
    }

    public void startShader(float duplicate) {
        GL11.glPushMatrix();
        GL20.glUseProgram(this.program);
        if (this.uniformsMap == null) {
            this.uniformsMap = new HashMap<String, Integer>();
            this.setupUniforms();
        }
        this.updateUniforms(duplicate);
    }

    public void stopShader() {
        GL20.glUseProgram(0);
        GL11.glPopMatrix();
    }

    public void setupUniforms() {

    }

    public void updateUniforms(float duplicate) {

    }

    private int createShader(final String shaderSource, final int shaderType) {
        int shader = 0;
        try {
            shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
            if (shader == 0) {
                return 0;
            }
            ARBShaderObjects.glShaderSourceARB(shader, (CharSequence)shaderSource);
            ARBShaderObjects.glCompileShaderARB(shader);
            if (ARBShaderObjects.glGetObjectParameteriARB(shader, 35713) == 0) {
                throw new RuntimeException("Error creating shader: " + this.getLogInfo(shader));
            }
            return shader;
        }
        catch (Exception e) {
            ARBShaderObjects.glDeleteObjectARB(shader);
            throw e;
        }
    }

    private String getLogInfo(final int i) {
        return ARBShaderObjects.glGetInfoLogARB(i, ARBShaderObjects.glGetObjectParameteriARB(i, 35716));
    }

    public void setUniform(final String uniformName, final int location) {
        this.uniformsMap.put(uniformName, location);
    }

    public void setupUniform(final String uniformName) {
        this.setUniform(uniformName, GL20.glGetUniformLocation(this.program, (CharSequence)uniformName));
    }

    public int getUniform(final String uniformName) {
        return this.uniformsMap.get(uniformName);
    }
}