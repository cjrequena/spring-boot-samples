package com.jahnelgroup.datarestsecurity.policy

import com.fasterxml.jackson.databind.ser.PropertyWriter
import com.jahnelgroup.datarestsecurity.club.Club
import com.jahnelgroup.jackson.security.SecureField
import com.jahnelgroup.jackson.security.policy.FieldSecurityPolicy

class ClubFieldPolicy : FieldSecurityPolicy {

    override fun permitAccess(secureField: SecureField, writer: PropertyWriter, target: Any, targetCreatedByUser: String?, currentPrincipalUser: String?): Boolean {
        if( target is Club){

            // not a member but I created the club.
            if( target.createdBy == currentPrincipalUser )
                return true

            return target.members.firstOrNull {
                it.createdBy == currentPrincipalUser
            } != null
        }
        return false
    }
}