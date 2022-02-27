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

uniform sampler2D gbuf_Position;
uniform sampler2D gbuf_Normal;
uniform sampler2D gbuf_Depth;
uniform sampler2D gbuf_Albedo;
uniform sampler2D gbuf_Specular;
uniform sampler2D gbuf_Emission;

in vec2 gbuf_Texcoord;

out vec4 FragColor;

float depth;
vec3 pos;
float shininess;
vec4 nrm;
vec4 alb;
vec3 specular;
vec3 emission;
vec3 viewDir;

uniform vec3 Eye;

#define IS_UNLIT nrm.w

void GetInputs(vec2 texcoord) {
    depth = texture(gbuf_Depth, texcoord).x;
    vec4 posSample = texture(gbuf_Position, texcoord);
    pos = posSample.xyz;
    shininess = posSample.w;
    nrm = texture(gbuf_Normal, texcoord).xyzw;
    nrm.xyz = normalize(nrm.xyz); //renormalize in G-buffer shader after bump map in normal shader
    alb = texture(gbuf_Albedo, texcoord).rgba;
    specular = texture(gbuf_Specular, texcoord).rgb;
    emission = texture(gbuf_Emission, texcoord).rgb;
    viewDir = normalize(Eye - pos);
}

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
    GetInputs(gbuf_Texcoord);
    FragColor = ShadeGBuffer();
}