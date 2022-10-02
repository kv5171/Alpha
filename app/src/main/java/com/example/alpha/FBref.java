package com.example.alpha;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FBref {
    final static FirebaseAuth auth = FirebaseAuth.getInstance();
    final static StorageReference storageRef = FirebaseStorage.getInstance().getReference();
}