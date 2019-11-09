#version 120

uniform sampler2D Texture;
uniform sampler2D LightMap;
varying vec2 p_TexCoord;
varying vec4 p_Color;
varying vec4 p_LightCoord;
varying vec4 p_LightCoordSun;
uniform float gamma;
uniform float sunlevel;
uniform float nightVisionWeight;

float getBrightness(float lightlevel)
{
    float f1 = 1.0 - lightlevel / 15.0;
    return (1.0 - f1) / (f1 * 3.0 + 1.0);
}

float applyGamma(float light)
{
    float lightC;
    light = clamp(light, 0.0, 1.0);
    lightC = 1.0 - light;
    light = light * (1.0 - gamma) + (1.0 - lightC * lightC * lightC * lightC) * gamma;
    light = 0.96 * light + 0.03;
    light = clamp(light, 0.0, 1.0);
    return light;
}

float normalize(float f)
{
    return f;
	/* return (f - 0.2) * 1.25; */
}

float doColor(float blockpart, float sunpart)
{
    float min = 0.05;
    float max = 1.0;
    float nightVisionMinBrightness = 0.7;
    min = min * (1.0 - nightVisionWeight) + nightVisionMinBrightness * nightVisionWeight;

    float brightness = getBrightness(blockpart) + normalize(sunlevel) * getBrightness(sunpart);
    return applyGamma(brightness) * (max - min) + min;
}

void main() {
    float block_r = p_LightCoord.x;
    float block_g = p_LightCoord.y;
    float block_b = p_LightCoord.z;
    float sun_r = p_LightCoordSun.x;
    float sun_g = p_LightCoordSun.y;
    float sun_b = p_LightCoordSun.z;

    float mixed_r = doColor(block_r, sun_r);
    float mixed_g = doColor(block_g, sun_g);
    float mixed_b = doColor(block_b, sun_b);

    vec4 color = vec4(mixed_r, mixed_g, mixed_b, 1);
    gl_FragColor = texture2D(Texture, p_TexCoord) * p_Color * (color);
}
