/*
package com.labstyle.darioandroid.fragments.connected_apps.models

import android.bluetooth.BluetoothDevice
import android.view.View
import android.widget.CheckBox
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.labstyle.darioandroid.Dario
import com.labstyle.darioandroid.analytics.AnalyticsEvents
import com.labstyle.darioandroid.analytics.AnalyticsParams
import com.labstyle.darioandroid.analytics.event.AnalyticsEvent
import com.labstyle.darioandroid.analytics.event.InfoPopoverEvent
import com.labstyle.darioandroid.crosslibtools.eventBus.DarioEventBus
import com.labstyle.darioandroid.fragments.connected_apps.adapters.ItemListener
import com.labstyle.darioandroid.fragments.connected_apps.data.ConnectedItem
import com.labstyle.darioandroid.fragments.connected_apps.data.UnpairDialogConfig
import com.labstyle.darioandroid.fragments.connected_apps.repos.ConnectedAppsRepo
import com.labstyle.darioandroid.fragments.connected_apps.views.ConnectedAppsFragmentNew.Companion.BLOOD_PRESSURE
import com.labstyle.darioandroid.fragments.connected_apps.views.ConnectedAppsFragmentNew.Companion.RUN_KEEPER
import com.labstyle.darioandroid.fragments.connected_apps.views.IConnectedAppsView
import com.labstyle.darioandroid.fragments.settings.fitness.eventBusEvents.ConnectedAppEnablingEvent
import com.labstyle.darioandroid.fragments.settings.fitness.sync.FitnessAppSyncHelper
import com.labstyle.darioandroid.gsmdevice.models.GsmDevice
import com.labstyle.darioandroid.gsmdevice.models.GsmDeviceType
import com.labstyle.darioandroid.resource.ResourceKey
import com.labstyle.darioandroid.rest.RestApiUser
import com.labstyle.darioandroid.rest.model.ClinicInvitationModel
import com.labstyle.darioandroid.rest.model.ClinicInvitationModelAdapter
import com.labstyle.darioandroid.utils.GlobalConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ConnectedAppsViewModel : ViewModel(), ItemListener {

    var itemList = MutableLiveData<List<ConnectedItem>>()
    var connectedAppsView: IConnectedAppsView? = null

    var topInfoText = MutableLiveData("")
    var proximityText = MutableLiveData("")
    private val bus = DarioEventBus.getInstance()

    init {
        //init base items list
        itemList.value = ConnectedItems.generateItems()
    }

    //Clinic items update
    fun updatedClinicItems() {

        setEnabled(DeviceIndicator.SCALE_WIFI, false)
        setEnabled(DeviceIndicator.SCALE_GSM, false)
        setEnabled(DeviceIndicator.BPM_GSM, false)

        CoroutineScope(Dispatchers.IO).launch {
            ConnectedAppsRepo.updateClinicItems { result ->
                //for (model in ConnectedItems.getMockTestingClinicData()) { //mock testing data
                for (model in result) {

                    if (model.deviceType != null) {
                        when (model.deviceType) {
                            ClinicInvitationModelAdapter.WIFI_SCALE -> {
                                updateItems(DeviceIndicator.SCALE_WIFI, model)
                            }
                            ClinicInvitationModelAdapter.GSM_SCALE -> {
                                updateItems(DeviceIndicator.SCALE_GSM, model)
                            }
                            ClinicInvitationModelAdapter.GSM_BPM -> {
                                updateItems(DeviceIndicator.BPM_GSM, model)
                            }
                        }
                        continue
                    }
                }
            }
        }
    }

    //Update items with clinic data
    private fun updateItems(deviceIndicator: DeviceIndicator, clinicInvitationModel: ClinicInvitationModel) {

        val isScaleDevicePaired = GlobalConstants.GRANTED == clinicInvitationModel.status
        itemList.value?.find { it.device == deviceIndicator }?.connected = isScaleDevicePaired
        itemList.value?.find { it.device == deviceIndicator }?.clinicInvitationModel = clinicInvitationModel
        itemList.notifyObserver()
    }

    //Checkbox click
    override fun onItemCheckedClicked(item: ConnectedItem, view: View) {
        val isChecked = (view as CheckBox).isChecked

        when (item.device) {
            DeviceIndicator.SCALE_WIFI -> initScaleWiFi(isChecked, item.clinicInvitationModel)
            DeviceIndicator.SCALE_GSM -> initScaleGSM(isChecked, item.clinicInvitationModel)
            DeviceIndicator.BPM_GSM -> initBpGSM(isChecked, item.clinicInvitationModel)
            DeviceIndicator.RUN_KEEPER -> initRunKeeper(isChecked)
            DeviceIndicator.CONTOUR -> initContour(isChecked)
            DeviceIndicator.BPM_BT -> initBpBT(isChecked)
            DeviceIndicator.PEDOMETER -> initPedometer(isChecked)
            DeviceIndicator.BPM_G2_BT -> initBpBTGen2(isChecked)
        }
    }

    //Click on image
    override fun zoomImage(view: View, image: Int) {
        connectedAppsView?.zoomImage(view, image)
    }

    //Click on info button
    override fun infoImage(item: ConnectedItem) {
        when (item.device) {
            DeviceIndicator.BPM_BT -> showInfoPopupBpmBT()
            else -> {}
        }
    }

    private fun showInfoPopupBpmBT() {
        connectedAppsView?.showInfoPopupBpmBT()
    }

    private fun initRunKeeper(check: Boolean) {
        trackDeviceClickEvent("runkeeper", check)
        if (check) {
            connectedAppsView?.startExerciseServiceAuthorizationFragment()
        } else {
            FitnessAppSyncHelper.setRunKeeperAccessToken(null, Dario.getAppContext())
            FitnessAppSyncHelper.stopRunKeeperSync()
            bus.post(ConnectedAppEnablingEvent(false, RUN_KEEPER))
        }
    }

    private fun initScaleWiFi(check: Boolean, clinicInvitationModel: ClinicInvitationModel?) {
        trackDeviceClickEvent("scale wifi", check)
        postEvent(AnalyticsEvents.WEIGHT_CONNECT_TOGGLE)
        if (check) {
            connectedAppsView?.startScaleWifiConnectionActivity()
        } else if (clinicInvitationModel != null) {
            if (clinicInvitationModel.id != null) {
                RestApiUser.deleteInvitationStatus(clinicInvitationModel.id) {
                    updatedClinicItems()
                }
            }
        }
    }

    fun infoClick() {
        bus.post(InfoPopoverEvent("connected_apps"))
        connectedAppsView?.showInfoPopup()
    }

    private fun initScaleGSM(check: Boolean, clinicInvitationModel: ClinicInvitationModel?) {
        trackDeviceClickEvent("scale gsm", check)

        val isPaired = clinicInvitationModel != null && GlobalConstants.REQUESTED == clinicInvitationModel.status

        if (check) {
            val id: String? = clinicInvitationModel?.id
            bus.post(
                AnalyticsEvent(
                    if (isPaired) AnalyticsEvents.CONNECTED_APPS_DEVICES_SCALE_GSM_REQUESTED_CLICKED
                    else AnalyticsEvents.CONNECTED_APPS_DEVICES_SCALE_GSM_NOT_PAIRED_CLICKED
                )
            )
            val device = GsmDevice(GsmDeviceType.SCALE, isPaired, measurementTaken = false, hasGoodReception = false, null, id)
            connectedAppsView?.startGSMConnectionActivity(device)

        } else {
            postEvent(AnalyticsEvents.CONNECTED_APPS_DEVICES_SCALE_GSM_PAIRED_CLICKED)
            postEvent(AnalyticsEvents.SCALE_GSM_UNPAIR_POP_UP)
            val config = UnpairDialogConfig(connectedAppsView?.getStringResource(ResourceKey.ScaleGSMUnPairPopupTitle), {
                postEvent(AnalyticsEvents.SCALE_GSM_UNPAIR_POP_UP_UNPAIR_CLICKED)
                if (clinicInvitationModel?.id != null) {
                    RestApiUser.deleteInvitationStatus(clinicInvitationModel.id) {
                        updatedClinicItems()
                    }
                }
            }, {
                postEvent(AnalyticsEvents.SCALE_GSM_UNPAIR_POP_UP_UNPAIR_CANCEL_CLICKED)
                setEnabled(DeviceIndicator.SCALE_GSM, true)
            })
            connectedAppsView?.showUnpairDialog(config)
        }
    }

    private fun initContour(check: Boolean) {
        trackDeviceClickEvent("ascencia", check)
        val contourService = connectedAppsView?.getContourService()

        if (check) {
            postEvent(AnalyticsEvents.ASCENSIA_PAIR)
            connectedAppsView?.startContourPairActivity()

        } else {
            postEvent(AnalyticsEvents.ASCENSIA_UN_PAIR)
            val config = UnpairDialogConfig(connectedAppsView?.getStringResource(ResourceKey.AscensiaBGUnPairPopupTitle),
                {
                    contourService?.getPairedDevicesWithPermissions { bluetoothDevices ->
                        if (bluetoothDevices != null) {
                            val pairedDevices: List<BluetoothDevice> = ArrayList(bluetoothDevices)

                            pairedDevices.forEach {
                                contourService.unpairDevice(it)
                            }
                        }
                    }
                    postEvent(AnalyticsEvents.ASCENSIA_UNPAIR_POPUP_UNPAIR)
                },
                {
                    contourService?.getPairedDevicesWithPermissions {
                        val enabled: Boolean = !(it != null && it.isEmpty())
                        setEnabled(DeviceIndicator.CONTOUR, enabled)
                    }
                })
            connectedAppsView?.showUnpairDialog(config)
            postEvent(AnalyticsEvents.ASCENSIA_UNPAIR_POPUP_VIEW)

        }
    }

    private fun postEvent(event: String) {
        bus.post(AnalyticsEvent(event))
    }

    private fun initBpBT(check: Boolean) {
        trackDeviceClickEvent("bp ble", check)
        if (check) {
            connectedAppsView?.startBluetoothPermissionActivity()
        } else {
            connectedAppsView?.unpairBloodPressureControl()
            bus.post(ConnectedAppEnablingEvent(false, BLOOD_PRESSURE))
        }

    }

    private fun initBpBTGen2(check: Boolean) {
        trackDeviceClickEvent("bp ble gen 2", check)
        if (check) {
            connectedAppsView?.startBleBpGen2PairingActivity()
        } else {
            val config = UnpairDialogConfig(connectedAppsView?.getStringResource(ResourceKey.AscensiaBGUnPairPopupTitle), {
                Dario.getInstance().preferences.blebpGen2MeterMacAddress = null
                connectedAppsView?.disposeBleBpGen2Service()
            },
                {
                    setEnabled(DeviceIndicator.BPM_G2_BT, isBleBpm2Paired())
                }
            )
            setEnabled(DeviceIndicator.BPM_G2_BT, isBleBpm2Paired())
            connectedAppsView?.showUnpairDialog(config)
        }
    }

    private fun initBpGSM(check: Boolean, model: ClinicInvitationModel?) {
        trackDeviceClickEvent("bp gsm", check)

        val isPaired = model != null && GlobalConstants.REQUESTED == model.status

        if (check) {
            val id: String? = model?.id
            bus.post(AnalyticsEvent(if (isPaired) AnalyticsEvents.CONNECTED_APPS_DEVICES_BP_GSM_REQUESTED_CLICKED else AnalyticsEvents.CONNECTED_APPS_DEVICES_BG_GSM_NOT_PAIRED_CLICKED))
            val device = GsmDevice(GsmDeviceType.BP_METER, isPaired, measurementTaken = false, hasGoodReception = false, null, id)
            connectedAppsView?.startGSMConnectionActivity(device)
        } else {
            postEvent(AnalyticsEvents.CONNECTED_APPS_DEVICES_BP_GSM_PAIRED_CLICKED)
            postEvent(AnalyticsEvents.BP_GSM_UNPAIR_POP_UP)
            val config = UnpairDialogConfig(connectedAppsView?.getStringResource(ResourceKey.BPGSMUnPairPopupTitle),
                {
                    bus
                        .post(AnalyticsEvent(AnalyticsEvents.BP_GSM_UNPAIR_POP_UP_UNPAIR_CLICKED))
                    if (model != null) {
                        if (model.id != null) {
                            RestApiUser.deleteInvitationStatus(model.id) {
                                updatedClinicItems()
                            }
                        }
                    }
                }, {
                    postEvent(AnalyticsEvents.BP_GSM_UNPAIR_POP_UP_UNPAIR_CANCEL_CLICKED)
                    setEnabled(DeviceIndicator.BPM_GSM, true)
                })
            connectedAppsView?.showUnpairDialog(config)
        }
    }

    private fun trackDeviceClickEvent(device: String, enabled: Boolean) {
        val params = HashMap<String, String>()
        params[AnalyticsParams.CONNECTED_APPS_DEVICE] = device
        bus.post(
            AnalyticsEvent(
                if (enabled) AnalyticsEvents.CONNECTED_APPS_ENABLED else AnalyticsEvents.CONNECTED_APPS_DISABLED,
                params
            )
        )
    }

    private fun initPedometer(check: Boolean) {
        trackDeviceClickEvent("pedometer", check)
        connectedAppsView?.initPedometer(check)
    }

    //Set check box checked/unchecked
    fun setEnabled(device: DeviceIndicator, enabled: Boolean) {
        itemList.value?.find { it.device == device }?.connected = enabled
        itemList.notifyObserver()
    }

    //Notify about list changes for refresh
    fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }

    fun stringRes(key: String) = connectedAppsView?.getStringResource(key)

    //Check is Gen2 paired
    fun isBleBpm2Paired(): Boolean {
        return Dario.getInstance().preferences.blebpGen2MeterMacAddress != null
    }
}*/
