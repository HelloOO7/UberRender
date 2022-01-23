#version 400 core

uniform sampler2D gbuf_Position;
uniform sampler2D gbuf_Normal;
uniform sampler2D gbuf_Depth;
uniform sampler2D gbuf_Albedo;

in vec2 gbuf_Texcoord;

out vec4 FragColor;

float depth;
vec3 pos;
vec4 nrm;
vec4 alb;

#define IS_UNLIT nrm.w

void GetInputs(vec2 texcoord) {
	depth = texture2D(gbuf_Depth, texcoord).x;
	pos = texture2D(gbuf_Position, texcoord).xyz;
	nrm = texture2D(gbuf_Normal, texcoord).xyzw;
	alb = texture2D(gbuf_Albedo, texcoord).rgba;
}

float floatmod(float numer, float denom) {
    return numer - (denom * floor(numer/denom));
}

vec4 ShadeGBuffer() {
	return alb * clamp(dot(nrm.xyz, -vec3(0.0, 0.0, -1.0) + (IS_UNLIT)), 0.0, 1.0); //adds value beyond 1 if unlit, forcing fullbright
}

vec4 DebugRenderAllGBuffer() {
    vec2 inCoord = vec2(floatmod(gbuf_Texcoord.x, 0.5), floatmod(gbuf_Texcoord.y, 0.5)) * 2.0;
    GetInputs(inCoord);
    if (gbuf_Texcoord.x < 0.5) {
        if (gbuf_Texcoord.y < 0.5) {
		return vec4(pos, 1.0);
        }
        else {
		return vec4(nrm.xyz, 1.0);
        }
    }
    else {
        if (gbuf_Texcoord.y < 0.5) {
		return alb;
        }
        else {
		return ShadeGBuffer();
        }
    }
}

void main(void) {
	FragColor = DebugRenderAllGBuffer();
}