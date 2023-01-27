package com.michlindev.moviedownloader

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.michlindev.moviedownloader.data.Movie

object Notification {

    private const val CHANNEL_ID = "channel_id01"
    private const val NOTIFICATION_ID = 1


    fun showNotification(movies: List<Movie>, resource: Bitmap?, context: Context) {

        createNotificationChannel(context)

        lateinit var remoteCollapsedViews: RemoteViews
        lateinit var remoteExpandedViews: RemoteViews

        if (movies.size == 1) {
            remoteCollapsedViews = RemoteViews(MovieDownloader.appContext.packageName, R.layout.notification_normal)
            remoteExpandedViews = RemoteViews(MovieDownloader.appContext.packageName, R.layout.notification_expended_single)

            remoteCollapsedViews.setTextViewText(R.id.textViewNotificationNrSinMovieName, movies.first().title)
            remoteCollapsedViews.setImageViewBitmap(R.id.imageViewNotificationNrSinPoster, resource)

            remoteExpandedViews.setTextViewText(R.id.textViewHeadlineNotificationExtended, movies.first().title)
            remoteExpandedViews.setImageViewBitmap(R.id.imageViewNotificationExtended, resource)
            remoteExpandedViews.setTextViewText(R.id.textViewNotificationExMulYear, movies.first().year.toString())
            remoteExpandedViews.setTextViewText(R.id.textViewNotificationExMulGenre, "Genre ${movies.first().genres.joinToString(", ")}")
            remoteExpandedViews.setTextViewText(R.id.textViewNotificationExMulRating,"Rating ${movies.first().ratingString}")
        } else {
            remoteCollapsedViews = RemoteViews(MovieDownloader.appContext.packageName, R.layout.notification_normal_multiple)
            remoteExpandedViews = RemoteViews(MovieDownloader.appContext.packageName, R.layout.notification_expended_multiple)

            val movieNames = mutableListOf<String>()
            movies.forEach {
                movieNames.add(it.title)
            }
            remoteExpandedViews.setTextViewText(R.id.textViewMoviesExpandedMultiple, movieNames.joinToString("\n"))

        }


        //start this(MainActivity) on by Tapping notification
        val mainIntent = Intent(context, MainActivity::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val mainPIntent = PendingIntent.getActivity(
            context, 0,
            mainIntent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        builder.setSmallIcon(R.drawable.ic_notification_icon)
        builder.priority = NotificationCompat.PRIORITY_DEFAULT
        builder.setAutoCancel(true)
        builder.setContentIntent(mainPIntent)
        builder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
        builder.setCustomContentView(remoteCollapsedViews)
        builder.setCustomBigContentView(remoteExpandedViews)
        val notificationManagerCompat = NotificationManagerCompat.from(context)

        //TODO ask permission on start
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build())


    }

    private fun createNotificationChannel(context: Context) {
        val name: CharSequence = "My Notification"
        val description = "My notification description"
        //importance of your notification
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationChannel = NotificationChannel(CHANNEL_ID, name, importance)
        notificationChannel.description = description
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager!!.createNotificationChannel(notificationChannel)
    }
}