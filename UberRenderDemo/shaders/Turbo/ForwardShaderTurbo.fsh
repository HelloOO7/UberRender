#version 400 core

#define LIGHT_COLOR_DIF 0
#define LIGHT_COLOR_AMB 1
#define LIGHT_COLOR_SPC 2
#define LIGHT_COLOR_MAX 3

#define LIGHT_MAX_DIR 1
#define LIGHT_MAX_POINT 32
#define LIGHT_MAX_SPOT 32

#define LIGHT_TYPE_DIR 0
#define LIGHT_TYPE_POINT 1
#define LIGHT_TYPE_SPOT 2
#define LIGHT_TYPE_MAX 3

#define LIGHT_ATTN_CONST 0
#define LIGHT_ATTN_LIN 1
#define LIGHT_ATTN_QUAD 2

struct DirLight {
    vec3 Direction;
    vec3 Colors[LIGHT_COLOR_MAX];
};

struct PointLight {
    vec3 Position;
    vec3 Colors[LIGHT_COLOR_MAX];  

    vec3 Attn;
};

struct SpotLight {
    vec3 Position;
    vec3 SpotDirection;
    float Cutoff;
    vec3 Colors[LIGHT_COLOR_MAX];  

    vec3 Attn;
};

uniform int LightCounts[LIGHT_TYPE_MAX];

uniform DirLight DirLights[LIGHT_MAX_DIR];
uniform PointLight PointLights[LIGHT_MAX_POINT];
uniform SpotLight SpotLights[LIGHT_MAX_SPOT];

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

layout(location = 0) out vec4 FragColor;

uniform vec3 Eye;

vec3 pos;
float shininess;
vec4 nrm;
vec4 alb;
vec3 specular;
vec3 emission;
vec3 viewDir;

vec2 matcap(vec3 eye, vec3 normal) {
  vec3 reflected = reflect(normalize(eye), normal);
  float m = 2.8284271247461903 * sqrt( reflected.z+1.0 );
  return reflected.xy / m + 0.5;
}

#define IS_UNLIT nrm.w

vec3 CalcDiffAmb(vec3 lightDirN, vec3 ambColor, vec3 diffColor) {
    vec3 amb = ambColor;
    vec3 diff = max(dot(nrm.xyz, -lightDirN), 0.0) * diffColor;
    return (diff + amb) * alb.xyz;
}

vec3 CalcSpecular(vec3 lightDirN, vec3 spcColor) {
    vec3 spc;
    if (dot(nrm.xyz, -lightDirN) < 0.0)
    {
        spc = vec3(0.0, 0.0, 0.0);
    }
    else {
        vec3 reflectDir = reflect(lightDirN, nrm.xyz);
        spc = pow(max(dot(viewDir, reflectDir), 0.0), shininess) * specular.xyz * spcColor;
    }
    return spc;
}

vec3 CalcDirLight(DirLight l) {
    vec3 lightDirN = normalize(l.Direction);
    vec3 diffAmb = CalcDiffAmb(lightDirN, l.Colors[LIGHT_COLOR_AMB], l.Colors[LIGHT_COLOR_DIF]);

    vec3 spc = CalcSpecular(lightDirN, l.Colors[LIGHT_COLOR_SPC]);

    return diffAmb + spc;
}

vec3 CalcPointLight(PointLight l)
{
    vec3 posDiff = pos - l.Position;

    vec3 lightDirN = normalize(posDiff);
    
    vec3 diffAmb = CalcDiffAmb(lightDirN, l.Colors[LIGHT_COLOR_AMB], l.Colors[LIGHT_COLOR_DIF]);
    vec3 spc = CalcSpecular(lightDirN, l.Colors[LIGHT_COLOR_SPC]);
    
    float distance    = length(posDiff);
    float attenuation = 1.0 / (l.Attn[LIGHT_ATTN_CONST] + l.Attn[LIGHT_ATTN_LIN] * distance + l.Attn[LIGHT_ATTN_QUAD] * (distance * distance));    

    return (diffAmb + spc) * attenuation;
}

vec3 CalcSpotLight(SpotLight l) {
    vec3 posDiff = pos - l.Position;

    vec3 lightDirN = normalize(posDiff);

    float theta = dot(lightDirN, normalize(l.SpotDirection));
    
    if(theta > l.Cutoff) 
    {
        vec3 diffAmb = CalcDiffAmb(lightDirN, l.Colors[LIGHT_COLOR_AMB], l.Colors[LIGHT_COLOR_DIF]);
        vec3 spc = CalcSpecular(lightDirN, l.Colors[LIGHT_COLOR_SPC]);
        
        float distance    = length(posDiff);
        float attenuation = 1.0 / (l.Attn[LIGHT_ATTN_CONST] + l.Attn[LIGHT_ATTN_LIN] * distance + l.Attn[LIGHT_ATTN_QUAD] * (distance * distance));    

        return (diffAmb + spc) * attenuation;
    }

    return vec3(0.0);
}

vec3 CalcLighting() {
    vec3 lighting = vec3(0.0);

    for (int i = 0; i < LightCounts[LIGHT_TYPE_DIR]; i++) {
        lighting += CalcDirLight(DirLights[i]);
    }

    for (int i = 0; i < LightCounts[LIGHT_TYPE_POINT]; i++) {
        lighting += CalcPointLight(PointLights[i]);
    }

    for (int i = 0; i < LightCounts[LIGHT_TYPE_SPOT]; i++) {
        lighting += CalcSpotLight(SpotLights[i]);
    }

    lighting += emission;
    lighting = mix(lighting, alb.xyz, IS_UNLIT);

    return lighting;
}

vec4 ShadeGBuffer() {
    return vec4(CalcLighting(), alb.a);
}

void main(void) {
	alb = texture(TexAlb, FS_Texcoord0);

	if (alb.a == 0.0) {
		discard;
	}

	mat3 tbn = mat3(FS_Tangent, cross(FS_Tangent, FS_Normal), FS_Normal);

	vec3 nrmValue = vec3(texture(TexNrm, FS_Texcoord0).xy, 1.0) * 2.0 - 1.0;

	nrm = vec4(normalize(tbn * mix(vec3(0.0, 0.0, 1.0), nrmValue, NrmIntensity)), 0.0);

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
		vec4 mtl = texture(TexMtl, matcap(Eye, nrm.xyz));
		alb.xyz += mtl.xyz * MtlIntensity;
	}

	alb.a *= AlphaScale;

	if (SpmEnable) {
		specular.xyz = texture(TexSpm, FS_Texcoord0).xyz * SpmIntensity;
	}
	else {
		specular.xyz = vec3(0.0);
	}

	if (EmmEnable) {
		emission = texture(TexEmm, FS_Texcoord0).xyz * EmmIntensity;
	}
	else {
		emission = vec3(0.0);
	}

	shininess = SpmShininess;
	pos = FS_View;
	viewDir = normalize(Eye - pos);

	FragColor = ShadeGBuffer();
}