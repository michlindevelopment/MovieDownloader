package com.michlindev.moviedownloader.debug

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.NotificationTarget
import com.bumptech.glide.request.transition.Transition
import com.michlindev.moviedownloader.MainActivity
import com.michlindev.moviedownloader.R
import com.michlindev.moviedownloader.data.Movie
import com.michlindev.moviedownloader.databinding.DebugFragmentBinding
import com.michlindev.moviedownloader.main.MovieListRepo
import kotlinx.coroutines.launch


class DebugFragment : Fragment() {

    private val viewModel: DebugViewModel by activityViewModels()

    private val CHANNEL_ID = "channel_id01"
    private val NOTIFICATION_ID = 1


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = DebugFragmentBinding.inflate(layoutInflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.notification.observe(viewLifecycleOwner){

            lifecycleScope.launch {
                val movies = MovieListRepo.searchMovie("Die Hard")

                Glide.with(requireContext())
                    .asBitmap()
                    .load(movies[1].large_cover_image)
                    .into(object : CustomTarget<Bitmap>(){
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            showNotification(movies[1],resource)
                            //imageView.setImageBitmap(resource)
                        }
                        override fun onLoadCleared(placeholder: Drawable?) {
                            // this is called when imageView is cleared on lifecycle call or for
                            // some other reason.
                            // if you are referencing the bitmap somewhere else too other than this imageView
                            // clear it here as you can no longer have the bitmap
                        }
                    })



            }
        }

        return binding.root
    }

    private fun showNotification(movie: Movie, resource: Bitmap) {
        createNotificationChannel()



        val remoteCollapsedViews = RemoteViews(requireActivity().packageName, R.layout.notification_normal)
        val remoteExpandedViews = RemoteViews(requireActivity().packageName, R.layout.notification_expended)

        remoteCollapsedViews.setTextViewText(R.id.notif_movie_name,movie.title)
        remoteCollapsedViews.setImageViewBitmap(R.id.notif_image, resource);

        //start this(MainActivity) on by Tapping notification
        val mainIntent = Intent(requireContext(), MainActivity::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val mainPIntent = PendingIntent.getActivity(
            requireContext(), 0,
            mainIntent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
        builder.setSmallIcon(R.drawable.search_icon)
        builder.priority = NotificationCompat.PRIORITY_DEFAULT
        builder.setAutoCancel(true)
        builder.setContentIntent(mainPIntent)
        builder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
        builder.setCustomContentView(remoteCollapsedViews)
        builder.setCustomBigContentView(remoteExpandedViews)
        val notificationManagerCompat = NotificationManagerCompat.from(requireContext())
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build())


    }

    private fun createNotificationChannel() {
        val name: CharSequence = "My Notification"
        val description = "My notification description"
        //importance of your notification
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationChannel = NotificationChannel(CHANNEL_ID, name, importance)
        notificationChannel.description = description
        val notificationManager = requireActivity().getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager!!.createNotificationChannel(notificationChannel)
    }

}