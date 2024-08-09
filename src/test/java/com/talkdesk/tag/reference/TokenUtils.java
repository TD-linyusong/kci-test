/*
 * Talkdesk Confidential
 *
 * Copyright (C) Talkdesk Inc. 2019
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office. Unauthorized copying of this file, via any medium
 * is strictly prohibited.
 */
package com.talkdesk.tag.reference;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.minidev.json.parser.JSONParser.DEFAULT_PERMISSIVE_MODE;

public final class TokenUtils {

    private TokenUtils() {
        // no-op: utility class
    }

    public static JsonWebToken token(final String id, final String name) {
        return new JsonWebToken() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public Set<String> getClaimNames() {
                return Set.of("aid", "uid", "aud");
            }

            @Override
            public <T> T getClaim(final String claim) {
                switch (claim) {
                    case "uid": // let's also return the name for the ID. It's just for logging
                    case "aid":
                        return (T) id;
                    case "aud":
                        return (T) List.of(name);
                    default:
                        return null;
                }
            }
        };
    }

    public static String generateTokenStringWithDefaultResource() {
        try {
            return generateTokenString("/jwt.json");
        } catch (Exception e) {
            throw new RuntimeException("There was a problem with the generation of the token");
        }
    }

    /**
     * Utility method to generate a JWT string from a JSON resource file that is signed by the privateKey.pem
     * test resource key.
     *
     * @param jsonResName - name of test resources file
     * @return the JWT string
     * @throws Exception on parse failure
     */
    public static String generateTokenString(final String jsonResName) throws Exception {

        final InputStream contentInputStream = TokenUtils.class.getResourceAsStream(jsonResName);
        final byte[] tmp = new byte[4096];
        final int length = contentInputStream.read(tmp);
        final byte[] content = new byte[length];
        System.arraycopy(tmp, 0, content, 0, length);

        return generateTokenStringFromJsonContent(new String(content, StandardCharsets.UTF_8));
    }

    /**
     * Utility method to generate a JWT string from a JSON content that is signed by the privateKey.pem
     * test resource key.
     *
     * @param jwtContentString - JSon content
     * @return the JWT string
     * @throws Exception on parse failure
     */
    public static String generateTokenStringFromJsonContent(final String jwtContentString) throws Exception {
        final JSONParser parser = new JSONParser(DEFAULT_PERMISSIVE_MODE);
        final JSONObject jwtContent = (JSONObject) parser.parse(jwtContentString);

        return generateTokenString(jwtContent, Collections.emptySet());
    }

    /**
     * Utility method to generate a JWT string from a JSON resource file that is signed by the privateKey.pem
     * test resource key.
     *
     * @param id - Id of the account for whom token is needed
     * @return the JWT string
     * @throws Exception on parse failure
     */
    public static String generateTokenStringForAccount(final String id) throws Exception {
        final String jwtContentString = "{\n" +
                "  \"iss\": \"https://konoha.talkdeskid.com\",\n" +
                "  \"sub\": \"eaVX54m5nJ3wH0ZGeFE2ae92LgM1u5bbvTW5iV1m\",\n" +
                "  \"aud\": \"https://api.talkdeskapp.com\",\n" +
                "  \"exp\": 1622126936,\n" +
                "  \"nbf\": 1558968536,\n" +
                "  \"jti\": \"Qt7lfhu1iM4WSTuumpypQPsSvJJq85OnqLI4kfC0\",\n" +
                "  \"cid\": \"02ri2Q8iZbIKnYc9LOduo6vx1zyEfDGnRxgjVeil\",\n" +
                "  \"gty\": \"authorization_code\",\n" +
                "  \"usr\": \"naruto.uzumaki@konoha.com\",\n" +
                "  \"tid\": \"td\",\n" +
                "  \"scp\": [\n" +
                "    \"openid\",\n" +
                "    \"apps:write\"\n" +
                "  ],\n" +
                "  \"groups\": [\n" +
                "\n" +
                "  ],\n" +
                "  \"aid\": \"" + id + "\"\n" +
                "}";

        return generateTokenStringFromJsonContent(jwtContentString);
    }

    /**
     * Utility method to generate a JWT string from a JSON resource file that is signed by the privateKey.pem
     * test resource key, possibly with invalid fields.
     *
     * @param jwtContent    - content of jwt token
     * @param invalidClaims - the set of claims that should be added with invalid values to test failure modes
     * @return the JWT string
     * @throws Exception on parse failure
     */
    private static String generateTokenString(final JSONObject jwtContent, final Set<InvalidClaims> invalidClaims) throws Exception {
        return generateTokenString(jwtContent, invalidClaims, null);
    }

    /**
     * Utility method to generate a JWT string from a JSON resource file that is signed by the privateKey.pem
     * test resource key, possibly with invalid fields.
     *
     * @param jwtContent    - content of jwt token
     * @param invalidClaims - the set of claims that should be added with invalid values to test failure modes
     * @param timeClaims    - used to return the exp, iat, auth_time claims
     * @return the JWT string
     * @throws Exception on parse failure
     */
    private static String generateTokenString(final JSONObject jwtContent, final Set<InvalidClaims> invalidClaims,
                                              final Map<String, Long> timeClaims) throws Exception {
        // Use the test private key associated with the test public key for a valid signature
        final PrivateKey pk = readPrivateKey("/privateKey.pem");
        return generateTokenString(pk, "/privateKey.pem", jwtContent, invalidClaims, timeClaims);
    }

    /**
     * Utility method to generate a JWT string from a JSON resource file that is signed by the privateKey.pem
     * test resource key, possibly with invalid fields.
     *
     * @param inputPk            - the private key to sign the token with
     * @param kid                - the kid claim to assign to the token
     * @param jwtContent         - content of jwt token
     * @param inputInvalidClaims - the set of claims that should be added with invalid values to test failure modes
     * @param timeClaims         - used to return the exp, iat, auth_time claims
     * @return the JWT string
     * @throws Exception on parse failure
     */
    private static String generateTokenString(final PrivateKey inputPk, final String kid, final JSONObject jwtContent,
                                              final Set<InvalidClaims> inputInvalidClaims,
                                              final Map<String, Long> timeClaims) throws Exception {

        final Set<InvalidClaims> invalidClaims = inputInvalidClaims != null ? inputInvalidClaims : Collections.emptySet();
        PrivateKey pk = inputPk;

        // Change the issuer to INVALID_ISSUER for failure testing if requested
        if (invalidClaims.contains(InvalidClaims.ISSUER)) {
            jwtContent.put(Claims.iss.name(), "INVALID_ISSUER");
        }
        final long currentTimeInSecs = currentTimeInSecs();
        long exp = currentTimeInSecs + 3000000;
        long iat = currentTimeInSecs;
        long authTime = currentTimeInSecs;
        boolean expWasInput = false;
        // Check for an input exp to override the default of now + 300 seconds
        if (timeClaims != null && timeClaims.containsKey(Claims.exp.name())) {
            exp = timeClaims.get(Claims.exp.name());
            expWasInput = true;
        }
        // iat and auth_time should be before any input exp value
        if (expWasInput) {
            iat = exp - 5;
            authTime = exp - 5;
        }
        jwtContent.put(Claims.iat.name(), iat);
        jwtContent.put(Claims.auth_time.name(), authTime);
        // If the exp claim is not updated, it will be an old value that should be seen as expired
        if (!invalidClaims.contains(InvalidClaims.EXP)) {
            jwtContent.put(Claims.exp.name(), exp);
        }
        // Return the token time values if requested
        if (timeClaims != null) {
            timeClaims.put(Claims.iat.name(), iat);
            timeClaims.put(Claims.auth_time.name(), authTime);
            timeClaims.put(Claims.exp.name(), exp);
        }

        if (invalidClaims.contains(InvalidClaims.SIGNER)) {
            // Generate a new random private key to sign with to test invalid signatures
            final KeyPair keyPair = generateKeyPair(2048);
            pk = keyPair.getPrivate();
        }

        // Create RSA-signer with the private key
        JWSSigner signer = new RSASSASigner(pk);
        final JWTClaimsSet claimsSet = JWTClaimsSet.parse(jwtContent);
        JWSAlgorithm alg = JWSAlgorithm.RS256;
        if (invalidClaims.contains(InvalidClaims.ALG)) {
            alg = JWSAlgorithm.HS256;
            final SecureRandom random = new SecureRandom();
            final BigInteger secret = BigInteger.probablePrime(256, random);
            signer = new MACSigner(secret.toByteArray());
        }
        final JWSHeader jwtHeader = new JWSHeader.Builder(alg)
                .keyID(kid)
                .type(JOSEObjectType.JWT)
                .build();
        final SignedJWT signedJWT = new SignedJWT(jwtHeader, claimsSet);
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    /**
     * Read a classpath resource into a string and return it.
     *
     * @param resName - classpath resource name
     * @return the resource content as a string
     * @throws IOException - on failure
     */
    private static String readResource(final String resName) throws IOException {
        final InputStream is = TokenUtils.class.getResourceAsStream(resName);
        final StringWriter sw = new StringWriter();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line = br.readLine();
            while (line != null) {
                sw.write(line);
                sw.write('\n');
                line = br.readLine();
            }
        }
        return sw.toString();
    }

    /**
     * Read a PEM encoded private key from the classpath
     *
     * @param pemResName - key file resource name
     * @return PrivateKey
     * @throws Exception on decode failure
     */
    private static PrivateKey readPrivateKey(final String pemResName) throws Exception {
        final InputStream contentIS = TokenUtils.class.getResourceAsStream(pemResName);
        final byte[] tmp = new byte[4096];
        final int length = contentIS.read(tmp);
        return decodePrivateKey(new String(tmp, 0, length));
    }

    /**
     * Read a PEM encoded public key from the classpath
     *
     * @param pemResName - key file resource name
     * @return PublicKey
     * @throws Exception on decode failure
     */
    private static PublicKey readPublicKey(final String pemResName) throws Exception {
        final InputStream contentIS = TokenUtils.class.getResourceAsStream(pemResName);
        final byte[] tmp = new byte[4096];
        final int length = contentIS.read(tmp);
        return decodePublicKey(new String(tmp, 0, length));
    }

    /**
     * Generate a new RSA keypair.
     *
     * @param keySize - the size of the key
     * @return KeyPair
     * @throws NoSuchAlgorithmException on failure to load RSA key generator
     */
    private static KeyPair generateKeyPair(final int keySize) throws NoSuchAlgorithmException {
        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);
        return keyPairGenerator.genKeyPair();
    }

    /**
     * Decode a PEM encoded private key string to an RSA PrivateKey
     *
     * @param pemEncoded - PEM string for private key
     * @return PrivateKey
     * @throws Exception on decode failure
     */
    private static PrivateKey decodePrivateKey(final String pemEncoded) throws Exception {
        final byte[] encodedBytes = toEncodedBytes(pemEncoded);

        final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedBytes);
        final KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(keySpec);
    }

    /**
     * Decode a PEM encoded public key string to an RSA PublicKey
     *
     * @param pemEncoded - PEM string for private key
     * @return PublicKey
     * @throws Exception on decode failure
     */
    private static PublicKey decodePublicKey(final String pemEncoded) throws Exception {
        final byte[] encodedBytes = toEncodedBytes(pemEncoded);

        final X509EncodedKeySpec spec = new X509EncodedKeySpec(encodedBytes);
        final KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    private static byte[] toEncodedBytes(final String pemEncoded) {
        final String normalizedPem = removeBeginEnd(pemEncoded);
        return Base64.getDecoder().decode(normalizedPem);
    }

    private static String removeBeginEnd(final String initialPem) {
        String pem = initialPem.replaceAll("-----BEGIN (.*)-----", "");
        pem = pem.replaceAll("-----END (.*)----", "");
        pem = pem.replaceAll("\r\n", "");
        pem = pem.replaceAll("\n", "");
        return pem.trim();
    }

    /**
     * @return the current time in seconds since epoch
     */
    private static int currentTimeInSecs() {
        final long currentTimeMillis = System.currentTimeMillis();
        return (int) (currentTimeMillis / 1000);
    }

    /**
     * Enums to indicate which claims should be set to invalid values for testing failure modes
     */
    public enum InvalidClaims {
        ISSUER, // Set an invalid issuer
        EXP,    // Set an invalid expiration
        SIGNER, // Sign the token with the incorrect private key
        ALG, // Sign the token with the correct private key, but HS
    }
}
