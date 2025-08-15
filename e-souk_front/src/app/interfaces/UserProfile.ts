export interface UserProfile {
    id: String;
    username: String;
    email: String;
    picture: String;
    phone: String;
    address: String;
    codePostal: String;
    role:  String;
    isActive: Boolean;  
    joinedAt: Date;
    updatedAt: Date;
}