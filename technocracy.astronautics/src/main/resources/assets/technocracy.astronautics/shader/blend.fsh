#version 130
out vec4 FragColor;

uniform sampler2D scene;
uniform sampler2D bloomBlur;
uniform float exposure;

void main()
{
    const float gamma = 2.2;
    vec3 hdrColor = texture(scene, gl_TexCoord[0].st).rgb;
    vec3 bloomColor = texture(bloomBlur, gl_TexCoord[0].st).rgb;
    vec3 result = pow(bloomColor, vec3(1.0 / exposure));
    hdrColor += result; // additive blending
    // tone mapping
    //vec3 result = vec3(1.0) - exp(-hdrColor * exposure);
    // also gamma correct while we're at it
    //result = pow(result, vec3(1.0 / gamma));
    FragColor = vec4(hdrColor, 1.0);

    /*vec4 diffuse = texture2D(bloomBlur, gl_TexCoord[0].st);

    //Exposure
    vec3 hdr = vec3(1.0F) - exp(-diffuse.rgb * exposure);

    //Gamma
    hdr = pow(hdr, vec3(1.0F / 2.2));

    FragColor = vec4(hdr, diffuse.a);*/
}