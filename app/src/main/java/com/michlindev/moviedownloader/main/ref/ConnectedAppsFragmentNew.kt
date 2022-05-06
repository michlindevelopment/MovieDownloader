/*
package com.labstyle.darioandroid.fragments.connected_apps.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.UiThread
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.labstyle.darioandroid.Dario
import com.labstyle.darioandroid.R
import com.labstyle.darioandroid.abstractions.Callback
import com.labstyle.darioandroid.blebpgen2.pairing.BleBpGen2PairingActivity
import com.labstyle.darioandroid.blebpgen2.service.BleBpGen2Service
import com.labstyle.darioandroid.blebpgen2.service.BleBpGen2ServiceBinder
import com.labstyle.darioandroid.bloodPressure.api.BloodPressureControl
import com.labstyle.darioandroid.bloodPressure.api.BluetoothPermissionActivity
import com.labstyle.darioandroid.bloodPressure.api.PARINGRESULT
import com.labstyle.darioandroid.contourdevice.pairing.ContourPairActivity
import com.labstyle.darioandroid.contourdevice.service.ContourDeviceService
import com.labstyle.darioandroid.contourdevice.service.ContourDeviceServiceBinder
import com.labstyle.darioandroid.crosslibtools.eventBus.DarioEventBus
import com.labstyle.darioandroid.custom.spinner.DarioFullScreenTransparentDialog
import com.labstyle.darioandroid.databinding.FragmentConnectedAppsNewBinding
import com.labstyle.darioandroid.fragments.base.BaseTabFragment
import com.michlindev.moviedownloader.main.ConnectedItemAdapter
import com.labstyle.darioandroid.fragments.connected_apps.data.UnpairDialogConfig
import com.labstyle.darioandroid.fragments.connected_apps.models.ConnectedAppsViewModel
import com.labstyle.darioandroid.fragments.connected_apps.models.ConnectedItems
import com.labstyle.darioandroid.fragments.connected_apps.models.DeviceIndicator
import com.labstyle.darioandroid.fragments.header.HeaderControlType
import com.labstyle.darioandroid.fragments.header.HeaderFragment.HeaderControl
import com.labstyle.darioandroid.fragments.navigation.ScreenSwitchController
import com.labstyle.darioandroid.fragments.scale.ScaleWifiConnectionActivity
import com.labstyle.darioandroid.fragments.settings.fitness.BloodPressurePopupContent
import com.labstyle.darioandroid.fragments.settings.fitness.ExerciseServiceAuthorizationFragment
import com.labstyle.darioandroid.fragments.settings.fitness.eventBusEvents.ConnectedAppEnablingEvent
import com.labstyle.darioandroid.fragments.settings.fitness.sync.FitnessAppSyncHelper
import com.labstyle.darioandroid.gsmdevice.models.GsmDevice
import com.labstyle.darioandroid.gsmdevice.pairing.GsmDevicePairActivity
import com.labstyle.darioandroid.mainScreen.MainScreenFragment
import com.labstyle.darioandroid.popups.InfoAlertPopup
import com.labstyle.darioandroid.resource.ApplyLocalizationReason
import com.labstyle.darioandroid.resource.DarioResources
import com.labstyle.darioandroid.resource.ResourceKey
import com.labstyle.darioandroid.stepcounter.tracker.StepTrackerScheduler.Companion.cancelScheduledJobs
import com.labstyle.darioandroid.stepcounter.tracker.StepTrackerScheduler.Companion.scheduleStepsCounter
import com.labstyle.darioandroid.utils.GlobalConstants
import com.labstyle.darioandroid.utils.image.ZoomPictureActivity.Companion.show
import com.labstyle.darioandroid.utils.kotlin.resString
import com.labstyle.darioandroid.utils.permission.PermissionsHelper.Companion.showAppSettingsInOs
import com.michlindev.moviedownloader.main.IConnectedAppsView

class ConnectedAppsFragmentNew : BaseTabFragment(), IConnectedAppsView {

    companion object {
        val TAG: String = ScreenSwitchController.getFragmentTag(ConnectedAppsFragmentNew::class.java)

        const val ARGUMENT_APP_DEVICE_START = "argument_app_device_start"
        const val ARGUMENT_APP_DEVICE_START_PAIR_GSM_SCALE = "pair_gsm_scale"
        const val BLOOD_PRESSURE = "bloodpressure"
        const val RUN_KEEPER = "runkeeper"
    }






    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)
        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (mBloodPressureControl != null && result.resultCode == Activity.RESULT_OK) {
                pairBpBtDevice()
            } else if (mBloodPressureControl == null || result.resultCode == Activity.RESULT_CANCELED) {
                mBloodPressureControl?.isTaiDocBloodPressureDevicePaired(context)?.let { viewModel.setEnabled(DeviceIndicator.BPM_BT, it) }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = DataBindingUtil.inflate<FragmentConnectedAppsNewBinding>(
            inflater, R.layout.fragment_connected_apps_new,
            container,
            false
        )

        binding.adapter = ConnectedItemAdapter(listOf(), viewModel)
        viewModel.itemList.observe(viewLifecycleOwner) {
            binding.adapter?.notifyDataSetChanged()
        }

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.proximitySpinner.setItems(ConnectedItems.getSpinnerItems(), FitnessAppSyncHelper.getSyncTimeIntervalIndex()) {
            FitnessAppSyncHelper.setSyncTimeIntervalIndex(it)
        }

        viewModel.connectedAppsView = this
        mBloodPressureControl =
            if (GlobalConstants.LOCALE_FALSE == mApp.darioResources.getString(ResourceKey.DisableBloodPressurePairing)) BloodPressureControl.getInstance() else null
        darioFullScreenTransparentDialog =
            DarioFullScreenTransparentDialog(this.context, getStringResource(ResourceKey.SWFBPPairing), null, false, R.raw.icon_blood_pressure_pairing)

        if (mBloodPressureControl?.isTaiDocBloodPressureDevicePaired(requireContext()) == true){
            viewModel.setEnabled(DeviceIndicator.BPM_BT,true)
        }
        return binding.root
    }

    override fun updateStateOnArgumentsChange(arguments: Bundle?) {
        handleDeepLinks(arguments)
        super.updateStateOnArgumentsChange(arguments)
    }

    private fun handleDeepLinks(args: Bundle?) {
        if (args?.getString(ARGUMENT_APP_DEVICE_START) != null) {
            if (ARGUMENT_APP_DEVICE_START_PAIR_GSM_SCALE == args.getString(ARGUMENT_APP_DEVICE_START)) {
                viewModel.setEnabled(DeviceIndicator.SCALE_GSM, true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updatedClinicItems()

        val contourServiceIntent = Intent(requireContext(), ContourDeviceService::class.java)
        requireContext().bindService(contourServiceIntent, contourServiceConnection, Context.BIND_AUTO_CREATE)
        val bleBpGen2ServiceIntent = Intent(requireContext(), BleBpGen2Service::class.java)
        requireContext().bindService(bleBpGen2ServiceIntent, bleBpGen2ServiceConnection, Context.BIND_AUTO_CREATE)

    }

    override fun onPause() {
        super.onPause()
        requireContext().unbindService(contourServiceConnection)
        requireContext().unbindService(bleBpGen2ServiceConnection)
    }

    override fun updateFragmentState() {
        mContainerActivity.sideMenuEnabled(false)
        mContainerActivity.mHeader.showControl(
            HeaderControlType.BACK, HeaderControl {
                mContainerActivity.switchMenuFragment(MainScreenFragment.TAG, false)
            }, null, null, false
        ).title = getStringResource(ResourceKey.DarioMenuItemTitleActivityProfile)
        mContainerActivity.mHeader.ignoreTitlePaddings()
    }

    override fun applyLocalization(res: DarioResources?, reason: ApplyLocalizationReason?) {
        viewModel.topInfoText.postValue(getStringResource(ResourceKey.SWFCTitleString))
        viewModel.proximityText.postValue(getStringResource(ResourceKey.SWFCProximityLabel))
    }

    override fun startExerciseServiceAuthorizationFragment() {
        mContainerActivity.switchMenuFragment(ExerciseServiceAuthorizationFragment.TAG, true)
    }

    override fun showUnpairDialog(config: UnpairDialogConfig) {
        if (confirmUnpair?.isShowing == true) {
            confirmUnpair?.dismiss()
        }

        val dialogBuilder = AlertDialog.Builder(requireContext(), R.style.DarioAlertDialog)
        dialogBuilder.setTitle(config.title)
        dialogBuilder.setMessage(getStringResource(ResourceKey.AscensiaBGUnPairPopupSubText))

        dialogBuilder.setPositiveButton(getStringResource(ResourceKey.AscensiaBGUnPairPopupButtonText)) { dialog, _ ->
            config.onUnpair?.run()
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton(getStringResource(ResourceKey.common_cancel)) { dialog, _ ->
            dialog.dismiss()
        }

        dialogBuilder.setOnDismissListener {
            config.onDismiss?.run()
        }

        confirmUnpair = dialogBuilder.create()
        confirmUnpair?.show()
    }

    private fun pairBpBtDevice() {
        mBloodPressureControl?.pairWithDevice(object : Callback<PARINGRESULT?> {

            @UiThread
            override fun onStarted() {
                darioFullScreenTransparentDialog?.show()
            }

            @UiThread
            override fun onError(msg: String) {
                darioFullScreenTransparentDialog?.dismiss()
            }

            @UiThread
            override fun onFinished(data: PARINGRESULT?) {
                darioFullScreenTransparentDialog?.dismiss()
                if (data != null && data.value > 1) {
                    showBloodPressurePairFailedPopup()
                }else {
                    DarioEventBus.getInstance().post(ConnectedAppEnablingEvent(true, BLOOD_PRESSURE))
                    viewModel.setEnabled(DeviceIndicator.BPM_BT,true)
                }
            }
        })
    }

    private fun showBloodPressurePairFailedPopup() {
        val content = BloodPressurePopupContent(
            ResourceKey.IPExercisePairingSomethingWentWrong,
            ResourceKey.IPExercisePairingNotCompleted
        )
        if (!mContainerActivity.popupsController.isAlertPopupShown) {
            showBloodPressurePopup(content)
        }
    }

    private fun showBloodPressurePopup(content: BloodPressurePopupContent) {
        val params: MutableMap<String, Any> = HashMap()
        params[InfoAlertPopup.TITLE_KEY] = getStringResource(
            content.titleKey ?: ResourceKey.IPExerciseProfileHelpBloodTitle
        )
        params[InfoAlertPopup.BODY_KEY] = getStringResource(content.bodyKey)
        params[InfoAlertPopup.OK_KEY] = getStringResource(ResourceKey.IPExerciseProfileHowToPairButton)
        mContainerActivity.popupsController.showAlertPopup(params)
    }

    override fun initPedometer(checked: Boolean) {
        if (checked) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val permission = ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACTIVITY_RECOGNITION
                )
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    mContainerActivity.performTaskOnPermissionsGranted({ granted: Boolean ->
                        if (granted) {
                            viewModel.setEnabled(DeviceIndicator.PEDOMETER, true)
                            Dario.getInstance().preferences.isPedometerEnabled = true
                            cancelScheduledJobs(requireContext(), false)
                            scheduleStepsCounter(requireContext().applicationContext)
                        } else {
                            showAppSettingsInOs(requireContext())
                        }
                    }, Manifest.permission.ACTIVITY_RECOGNITION)
                    return
                }
            }

            viewModel.setEnabled(DeviceIndicator.PEDOMETER, true)
            Dario.getInstance().preferences.isPedometerEnabled = true
            cancelScheduledJobs(requireContext(), false)
            scheduleStepsCounter(requireContext().applicationContext)
        } else {
            viewModel.setEnabled(DeviceIndicator.PEDOMETER, false)
            Dario.getInstance().preferences.isPedometerEnabled = false
            cancelScheduledJobs(requireContext().applicationContext, true)
        }
    }

    override fun showInfoPopup() {
        mContainerActivity.popupsController.showTopAlignedPopup(
            getStringResource(ResourceKey.IPExerciseProfileHelpTitle),
            getStringResource(ResourceKey.IPExerciseProfileHelpMessage),
            getStringResource(ResourceKey.QDECloseTitle)
        )
    }

    override fun showInfoPopupBpmBT() {
            showBloodPressurePopup(
                BloodPressurePopupContent(
                    null, ResourceKey.IPExerciseProfileHelpBloodMessage
                )
            )
    }

    override fun zoomImage(view: View, image: Int) {
        if (image != -1)
            show(requireActivity(), view, image)
    }

    override fun startScaleWifiConnectionActivity() {
        startActivity(Intent(activity, ScaleWifiConnectionActivity::class.java))
    }

    override fun startGSMConnectionActivity(device: GsmDevice) {
        GsmDevicePairActivity.show(requireContext(), device)
    }

    override fun startContourPairActivity() {
        startActivity(Intent(requireContext(), ContourPairActivity::class.java))
    }

    override fun startBleBpGen2PairingActivity() {
        BleBpGen2PairingActivity.show(requireContext())
    }

    override fun disposeBleBpGen2Service() {
        bleBpGen2Service?.dispose()
    }


    override fun startBluetoothPermissionActivity() {
        val intent = Intent(requireContext(), BluetoothPermissionActivity::class.java)
        startForResult.launch(intent)
    }

    override fun unpairBloodPressureControl() {
        mBloodPressureControl?.unpairWithDevice(requireContext())
    }

    override fun getContourService() = contourService
    override fun getStringResource(key: String) = Dario.getRes().resString(key)

}
*/
