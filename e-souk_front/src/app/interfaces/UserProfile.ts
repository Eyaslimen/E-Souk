export interface UserProfile {

    id: String;
    
    /**
     * Nom d'utilisateur unique
     */
    username: String;
    
    /**
     * Email de l'utilisateur
     */
    email: String;
    
    /**
     * Photo de profil (optionnelle)
     */
    picture: String;
    
    /**
     * Numéro de téléphone (optionnel)
     */
    phone: String;
    
    /**
     * Adresse postale (optionnelle)
     */
    address: String;
    
    /**
     * Code postal (optionnel)
     */
    codePostal: String;
    
    /**
     * Rôle de l'utilisateur
     */
    role:  String;
    
    /**
     * Indique si le compte est actif
     */
    isActive: Boolean;
    
    /**
     * Date de création du compte
     */
    joinedAt: Date;
    
    /**
     * Date de dernière modification du profil
     */
    updatedAt: Date;
}