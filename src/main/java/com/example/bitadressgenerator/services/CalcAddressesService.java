package com.example.bitadressgenerator.services;

import com.example.bitadressgenerator.model.BtcAddressEntinty;
import com.example.bitadressgenerator.repository.BtcAddressRepository;
import com.example.bitadressgenerator.utils.Base58;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECPoint;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class CalcAddressesService implements CommandLineRunner {

    private final BtcAddressRepository btcAddressRepository;

    @Override
    public void run(String... args) throws Exception {
        while (true) {
            try {
                Pair<String,String> p = genAddress();
                Optional<BtcAddressEntinty>  oe =  btcAddressRepository.findById(p.getSecond());
                if(oe.isPresent()){
                    BtcAddressEntinty e = oe.get();
                    e.setPrivateKey(p.getFirst());
                    log.debug("I FOOOOOOUND IT!!!!");
                    return;
                } else {
                    log.debug("NO MATCH :(");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Pair<String, String> genAddress() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        Security.addProvider(new BouncyCastleProvider());
        KeyPair pair = genKeys();
        String priv = getPriv(pair);
        String pub = getPub(pair);

        log.debug("priv: " + priv);
        log.debug("pub: " + pub);
        log.debug("-------------------------------");

        return Pair.of(priv, pub);
    }

    public KeyPair genKeys() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC", "BC");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
        keyGen.initialize(ecSpec);
        return keyGen.generateKeyPair();
    }

    private String adjustTo64(String s) {
        switch (s.length()) {
            case 62:
                return "00" + s;
            case 63:
                return "0" + s;
            case 64:
                return s;
            default:
                throw new IllegalArgumentException("not a valid key" + s);
        }
    }

    private String getPriv(KeyPair pair) {
        return adjustTo64(((ECPrivateKey) pair.getPrivate()).getS().toString(16));
    }

    public String getPub(KeyPair pair) throws NoSuchAlgorithmException, NoSuchProviderException {
        ECPublicKey epub = (ECPublicKey) pair.getPublic();
        ECPoint pt = epub.getW();
        String sx = adjustTo64(pt.getAffineX().toString(16));
        String sy = adjustTo64(pt.getAffineY().toString(16));
        String bcPub = "04" + sx + sy;

        byte[] bcPubBA = new byte[bcPub.length() / 2];
        for (int i = 0; i < bcPub.length() / 2; i += 2) {
            bcPubBA[i / 2] = (byte) ((Character.digit(bcPub.charAt(i), 16) << 4) + Character.digit(bcPub.charAt(i + 1), 16));
        }

        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] s1 = sha.digest(bcPubBA);

        MessageDigest rmd = MessageDigest.getInstance("RipeMD160", "BC");
        byte[] r1 = rmd.digest(s1);

        byte[] r2 = new byte[r1.length + 1];
        r2[0] = 0;
        for (int i = 0; i < r1.length; i++) r2[i + 1] = r1[i];

        byte[] s2 = sha.digest(r2);
        byte[] s3 = sha.digest(s2);

        byte[] a1 = new byte[25];
        for (int i = 0; i < r2.length; i++) {
            a1[i] = r2[i];
        }
        for (int i = 0; i < 4; i++) a1[21 + i] = s3[i];

        return Base58.encode(a1);
    }

    void checkAddress(String address){

    }
}
