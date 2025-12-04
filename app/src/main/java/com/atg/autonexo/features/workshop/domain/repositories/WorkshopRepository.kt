package com.atg.autonexo.features.workshop.domain.repositories

import android.net.Uri
import com.atg.autonexo.features.workshop.domain.models.AcceptInvitationRequest
import com.atg.autonexo.features.workshop.domain.models.CreateInvitationRequest
import com.atg.autonexo.features.workshop.domain.models.CreateWorkshopRequest
import com.atg.autonexo.features.workshop.domain.models.Invitation
import com.atg.autonexo.features.workshop.domain.models.Location
import com.atg.autonexo.features.workshop.domain.models.Workshop
import com.atg.autonexo.features.workshop.domain.models.WorkshopEmployee
import com.atg.autonexo.features.workshop.domain.models.WorkshopStaff

interface WorkshopRepository {
    suspend fun createWorkshop(request: CreateWorkshopRequest): Result<Workshop>
    suspend fun getWorkshopById(workshopId: Long): Result<Workshop>
    suspend fun getMyWorkshop(): Result<Workshop>
    suspend fun updateWorkshop(workshop: Workshop): Result<Workshop>


    //Photo
    suspend fun uploadLogo(workshopId: Long, imageUri: Uri): Result<String>
    suspend fun uploadPhoto(workshopId: Long, imageUri: Uri): Result<String>
    suspend fun deletePhoto(workshopId: Long, photoIndex: Int): Result<Unit>

    suspend fun getWorkshopLocations(workshopId: Long): Result<List<Location>>

    //Tag
    suspend fun updateTags(workshopId: Long, tags: List<String>): Result<Workshop>
    suspend fun addTags(workshopId: Long, tags: List<String>): Result<Workshop>

    //Location
    suspend fun addLocation(workshopId: Long, location: Location): Result<Location>
    suspend fun updateLocation(workshopId: Long, locationId: Long, location: Location): Result<Location>
    suspend fun deleteLocation(workshopId: Long, locationId: Long): Result<Unit>
    suspend fun getLocationById(workshopId: Long, locationId: Long): Result<Location>

    // Invitation
    suspend fun createInvitation(request: CreateInvitationRequest): Result<Invitation>
    suspend fun getInvitations(): Result<List<Invitation>>
    suspend fun getInvitationByCode(code: String): Result<Invitation>
    suspend fun acceptInvitation(request: AcceptInvitationRequest): Result<String>
    
    // Empleados
    suspend fun getWorkshopEmployees(workshopId: Long): Result<List<WorkshopEmployee>>
    suspend fun deactivateEmployee(workshopId: Long, employeeId: Long): Result<Unit>
    suspend fun activateEmployee(workshopId: Long, employeeId: Long): Result<Unit>

    suspend fun getWorkshopStaff(): Result<List<WorkshopStaff>>
}

