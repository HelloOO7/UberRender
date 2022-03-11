#version 400 core

uniform sampler2D TexAlb;
uniform sampler2D TexNrm;
uniform sampler2D TexSpm;
uniform sampler2D TexEmm;
uniform sampler2D TexShadowMap;
uniform sampler2D TexMtl;

uniform bool MtlEnable;
uniform float MtlIntensity;

uniform bool SpmEnable;
uniform float SpmIntensity;
uniform float SpmShininess;

uniform bool NrmEnable;
uniform float NrmIntensity;

uniform bool EmmEnable;
uniform float EmmIntensity;

uniform bool ShadowMapEnable;
uniform float ShadowMapIntensity;
uniform vec4 ShadowMapTransform;
uniform bool AOEnable;
uniform float AOIntensity;

uniform float AlphaScale;

in vec3 FS_Normal;
in vec3 FS_Tangent;
in vec3 FS_View;
in vec2 FS_Texcoord0;
in vec2 FS_Texcoord1;

layout(location = 0) out vec4 gbuf_Position;
layout(location = 1) out vec4 gbuf_Normal;
layout(location = 2) out vec4 gbuf_Albedo;
layout(location = 3) out vec4 gbuf_Specular;
layout(location = 4) out vec4 gbuf_Emission;

uniform vec3 Eye;

vec2 matcap(vec3 eye, vec3 normal) {
  vec3 reflected = reflect(normalize(eye), normal);
  float m = 2.8284271247461903 * sqrt( reflected.z+1.0 );
  return reflected.xy / m + 0.5;
}

void main(void) {
	vec4 alb = texture(TexAlb, FS_Texcoord0);

	if (alb.a == 0.0) {
		discard;
	}

	mat3 tbn = mat3(FS_Tangent, cross(FS_Tangent, FS_Normal), FS_Normal);

	vec3 nrmValue = vec3(texture(TexNrm, FS_Texcoord0).xy, 1.0) * 2.0 - 1.0;

	gbuf_Normal = vec4(tbn * mix(vec3(0.0, 0.0, 1.0), nrmValue, NrmIntensity), 1.0);
	gbuf_Position = vec4(FS_View, SpmShininess);

	if (ShadowMapEnable) {
		vec2 bakeUV = vec2(FS_Texcoord1.x, 1.0 - FS_Texcoord1.y) * ShadowMapTransform.xy + ShadowMapTransform.zw;
		bakeUV.y = 1.0 - bakeUV.y;
		vec3 bakeColor = texture(TexShadowMap, bakeUV).rgb;
		alb.rgb *= mix(vec3(1.0), bakeColor.ggg, ShadowMapIntensity);
		if (AOEnable) {
			alb.rgb *= mix(vec3(1.0), bakeColor.rrr, AOIntensity);
		}
	}

	if (MtlEnable) {
		vec4 mtl = texture(TexMtl, matcap(Eye, gbuf_Normal.xyz));
		alb.xyz += mtl.xyz * MtlIntensity;
	}

	alb.a *= AlphaScale;

	gbuf_Albedo = alb;
	
	if (SpmEnable) {
		gbuf_Specular.xyz = texture(TexSpm, FS_Texcoord0).xyz * SpmIntensity;
	}
	else {
		gbuf_Specular.xyz = vec3(0.0);
	}
	if (EmmEnable) {
		gbuf_Emission = vec4(texture(TexEmm, FS_Texcoord0).xyz * EmmIntensity, 1.0);
	}
	else {
		gbuf_Emission = vec4(0.0, 0.0, 0.0, 1.0);
	}
}