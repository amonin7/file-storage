var objFile;

function selectFile(Files) {
    objFile=Files[0];
    uploadButton.disabled = !(txtEncpassphrase.value.length >= 8 && objFile);
}

function encvalidate() {
    if(txtEncpassphrase.value.length>=8) {
        spnCheckretype.classList.add("greenspan");
        spnCheckretype.classList.remove("redspan");
        spnCheckretype.innerHTML='&#10004;';
    } else {
        spnCheckretype.classList.remove("greenspan");
        spnCheckretype.classList.add("redspan");
        spnCheckretype.innerHTML='&#10006;';
    }

    uploadButton.disabled = !(txtEncpassphrase.value.length >= 8 && objFile);
}

async function sendFile() {
    let oldFormData = new FormData();
    let encryptedFile = await encryptFile();
    console.log("Encrypted new file");
    let encFilename = objFile.name + '.enc';
    oldFormData.append("files", encryptedFile, encFilename);
    console.log("Set new file");
    let response = await fetch('/upload', {
        method: 'POST',
        body: oldFormData
    });
    console.log("Got response");
    let result = await response.json();
    if (response.ok) {
        console.log("File uploaded: " + result.name);
    } else {
        alert("The error occurred while sending file to server. Repeat Sending please");
    }
    fileInput.disabled = true;
    uploadForm.submit();
}

// TODO: Add passphrase into form.
let passphrase = "SOME_CUSTOM_PASSPHRASE"

function readFile(file){
    return new Promise((resolve, reject) => {
        var fr = new FileReader();
        fr.onload = () => {
            resolve(fr.result)
        };
        fr.readAsArrayBuffer(file);
    });
}

async function encryptFile() {
    var plainTextBytes=await readFile(objFile)
        .catch(function(err){
            console.error(err);
        });
    plainTextBytes=new Uint8Array(plainTextBytes);

    var pbkdf2iterations=10000;
    var passphraseBytes=new TextEncoder("utf-8").encode(passphrase);
    // var passphraseBytes=new TextEncoder("utf-8").encode(txtEncpassphrase.value);
    console.log("Passphrase is: " + txtEncpassphrase.value);
    var pbkdf2salt=window.crypto.getRandomValues(new Uint8Array(8));

    var passphrasekey=await window.crypto.subtle.importKey('raw', passphraseBytes, {name: 'PBKDF2'}, false, ['deriveBits'])
        .catch(function(err){
            console.error(err);
        });
    console.log('passphrasekey imported');

    var pbkdf2bytes=await window.crypto.subtle.deriveBits({"name": 'PBKDF2', "salt": pbkdf2salt, "iterations": pbkdf2iterations, "hash": 'SHA-256'}, passphrasekey, 384)
        .catch(function(err){
            console.error(err);
        });
    console.log('pbkdf2bytes derived');
    pbkdf2bytes=new Uint8Array(pbkdf2bytes);

    keybytes=pbkdf2bytes.slice(0,32);
    ivbytes=pbkdf2bytes.slice(32);

    var key=await window.crypto.subtle.importKey('raw', keybytes, {name: 'AES-CBC', length: 256}, false, ['encrypt'])
        .catch(function(err){
            console.error(err);
        });
    console.log('key imported');

    var cipherbytes=await window.crypto.subtle.encrypt({name: "AES-CBC", iv: ivbytes}, key, plainTextBytes)
        .catch(function(err){
            console.error(err);
        });

    if(!cipherbytes) {
        spnEncstatus.classList.add("redspan");
        spnEncstatus.innerHTML='<p>Error encrypting file.  See console log.</p>';
        return;
    }

    console.log('plaintext encrypted');
    cipherbytes=new Uint8Array(cipherbytes);

    var resultbytes=new Uint8Array(cipherbytes.length+16)
    resultbytes.set(new TextEncoder("utf-8").encode('Salted__'));
    resultbytes.set(pbkdf2salt, 8);
    resultbytes.set(cipherbytes, 16);

    return new Blob([resultbytes], {type: 'image/jpeg'});
}

async function deleteAll(e) {
    let response = await fetch('/delete', {
        method: 'POST'
    });
    if (!response.ok) {
        alert("The error occurred while sending file to server. Repeat Sending please");
    }
    deleteForm.submit()
}

async function decryptfile(decryptedFile, filename) {
    var cipherbytes=await readFile(decryptedFile)
        .catch(function(err){
            console.error(err);
        });
    cipherbytes=new Uint8Array(cipherbytes);

    console.log("decrypted file");
    var pbkdf2iterations=10000;
    var passphrasebytes=new TextEncoder("utf-8").encode(passphrase);
    // var passphrasebytes=new TextEncoder("utf-8").encode(txtDecpassphrase.value);
    var pbkdf2salt=cipherbytes.slice(8,16);

    var passphrasekey=await window.crypto.subtle.importKey('raw', passphrasebytes, {name: 'PBKDF2'}, false, ['deriveBits'])
        .catch(function(err){
            console.error(err);

        });
    console.log('passphrasekey imported');

    var pbkdf2bytes=await window.crypto.subtle.deriveBits({"name": 'PBKDF2', "salt": pbkdf2salt, "iterations": pbkdf2iterations, "hash": 'SHA-256'}, passphrasekey, 384)
        .catch(function(err){
            console.error(err);
        });
    console.log('pbkdf2bytes derived');
    pbkdf2bytes=new Uint8Array(pbkdf2bytes);

    keybytes=pbkdf2bytes.slice(0,32);
    ivbytes=pbkdf2bytes.slice(32);
    cipherbytes=cipherbytes.slice(16);

    var key=await window.crypto.subtle.importKey('raw', keybytes, {name: 'AES-CBC', length: 256}, false, ['decrypt'])
        .catch(function(err){
            console.error(err);
        });
    console.log('key imported');

    var plaintextbytes=await window.crypto.subtle.decrypt({name: "AES-CBC", iv: ivbytes}, key, cipherbytes)
        .catch(function(err){
            console.error(err);
        });

    if(!plaintextbytes) {
        console.log('Error decrypting file. Password may be incorrect');
        return;
    }
    console.log('ciphertext decrypted');
    plaintextbytes=new Uint8Array(plaintextbytes);

    var blob = new Blob([plaintextbytes], {type: 'application/download'});
    var downloadLink = document.createElement("a");
    console.log('filename is ' + filename);
    downloadLink.download = filename;
    downloadLink.href = URL.createObjectURL(blob);
    downloadLink.onclick = destroyClickedElement;
    downloadLink.style.display = "none";
    document.body.appendChild(downloadLink);
    downloadLink.click();
}

async function download(event) {
    console.log(event.target.name);
    let filename = event.target.value
    filename = filename.replace('.enc', '');
    let response = await fetch(event.target.name);
    console.log(response.status);
    let blob = await response.blob();
    await decryptfile(blob, filename);
}

function destroyClickedElement(event) {
    document.body.removeChild(event.target);
}
