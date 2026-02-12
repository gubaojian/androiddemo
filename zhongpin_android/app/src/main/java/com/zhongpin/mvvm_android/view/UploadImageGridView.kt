package com.zhongpin.mvvm_android.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter4.BaseMultiItemAdapter
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.engine.CompressFileEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.zhilianshidai.pindan.app.BuildConfig
import com.zhilianshidai.pindan.app.databinding.ListUploadImageAddItemBinding
import com.zhilianshidai.pindan.app.databinding.ListUploadImageShowImageBinding
import com.zhilianshidai.pindan.app.databinding.ViewUploadImageGridViewBinding
import com.zhongpin.lib_base.utils.BannerUtils
import com.zhongpin.lib_base.utils.LogUtils
import com.zhongpin.mvvm_android.photo.selector.GlideEngine
import com.zhongpin.mvvm_android.ui.photo.preview.PhonePreviewerActivity
import top.zibin.luban.Luban
import top.zibin.luban.OnNewCompressListener
import java.io.File

class UploadImageGridView @JvmOverloads constructor (context: Context,
                          attrs: AttributeSet? = null,
                          defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var uploadImageGridViewBinding: ViewUploadImageGridViewBinding;

    private lateinit var mAdapter: UploadListGridViewAdapter;
    private val mData: MutableList<UploadImageItem> = mutableListOf();

    //最大选择数量。
    var maxSelectNum = 4;

    init {
        uploadImageGridViewBinding = ViewUploadImageGridViewBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )
        val  gridLayoutManager = GridLayoutManager(context, 4);
        mData.add(UploadImageItem(isAdd = true, filePath = ""))
        mAdapter = UploadListGridViewAdapter(mData, this);
        uploadImageGridViewBinding.uploadImageRecyclerView.layoutManager = gridLayoutManager;
        uploadImageGridViewBinding.uploadImageRecyclerView.adapter = mAdapter;
    }

    fun getSelectImages(): MutableList<UploadImageItem> {
        val selectFiles: MutableList<UploadImageItem> = mutableListOf();
        for (item in mData) {
            if (!item.isAdd) {
                selectFiles.add(item);
            }
        }
        return selectFiles;
    }



    class UploadImageItem(
        val isAdd:Boolean,
        val filePath:String
    ) {

    }

    class UploadListGridViewAdapter(
        val data: MutableList<UploadImageItem>,
        val uploadImageGridView: UploadImageGridView
       ): BaseMultiItemAdapter<UploadImageItem>(data) {

        class AddVH(
            parent: ViewGroup,
            val binding: ListUploadImageAddItemBinding = ListUploadImageAddItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
        ) : RecyclerView.ViewHolder(binding.root)

        class ImageVH(
            parent: ViewGroup,
            val binding: ListUploadImageShowImageBinding = ListUploadImageShowImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
        ) : RecyclerView.ViewHolder(binding.root)

        init {

            addItemType(ADD_TYPE, object : OnMultiItemAdapterListener<UploadImageItem, AddVH> { // 类型 1
                override fun onCreate(context: Context, parent: ViewGroup, viewType: Int): AddVH {
                    return AddVH(parent)
                }

                override fun onBind(holder: AddVH, position: Int, item: UploadImageItem?) {

                    item?.let {
                        holder.binding.root.setOnClickListener {
                            val canSelectNum = uploadImageGridView.maxSelectNum - (data.size - 1);
                            PictureSelector.create(context)
                                .openGallery(SelectMimeType.ofImage())
                                .setImageEngine(GlideEngine.createGlideEngine())
                                .setCompressEngine(object: CompressFileEngine {
                                    override fun onStartCompress(
                                        context: Context?,
                                        source: java.util.ArrayList<Uri>?,
                                        call: OnKeyValueResultCallbackListener?
                                    ) {
                                        if (source == null || source.isEmpty()) {
                                            return;
                                        }
                                        Luban.with(context)
                                            .load(source)
                                            .ignoreBy(100).setCompressListener(
                                                object : OnNewCompressListener {
                                                    override fun onStart() {

                                                    }

                                                    override fun onSuccess(source: String?, compressFile: File?) {
                                                        if (call != null) {
                                                            if (compressFile != null) {
                                                                LogUtils.d(
                                                                    "UploadImageGridView",
                                                                    "UploadImageGridView compressFile " + source + " compress " + compressFile.absolutePath
                                                                            + " length " + compressFile.length() / 1024
                                                                )
                                                            }
                                                            call.onCallback(source, compressFile?.absolutePath);
                                                        }
                                                    }

                                                    override fun onError(source: String?, e: Throwable?) {
                                                        if (call != null) {
                                                            call.onCallback(source, null);
                                                        }
                                                    }
                                                }
                                            ).launch();
                                    }

                                })
                                .setMaxSelectNum(canSelectNum)
                                .forResult(object : OnResultCallbackListener<LocalMedia?> {
                                    override fun onResult(result: ArrayList<LocalMedia?>) {
                                        if (result.isNullOrEmpty()) {
                                            return;
                                        }
                                        val selectFiles: MutableList<UploadImageItem> = mutableListOf();
                                        for(localMedia in result) {
                                            if (localMedia == null) {
                                                continue;
                                            }
                                            if(BuildConfig.DEBUG) {
                                                Log.e("UploadImageGridView", "UploadImageGridView photo selector " + localMedia.path + " cut path " + localMedia.cutPath)
                                            }
                                            val filePath = localMedia.compressPath ?: localMedia.realPath;
                                            if (filePath.isNullOrEmpty()) {
                                                continue
                                            }
                                            selectFiles.add(UploadImageItem(isAdd = false, filePath = filePath))
                                        }
                                        uploadImageGridView.mData.addAll(0, selectFiles)
                                        if (uploadImageGridView.mData.size > uploadImageGridView.maxSelectNum) {
                                            uploadImageGridView.mData.removeAt(uploadImageGridView.mData.size-1);
                                        }
                                        notifyDataSetChanged()
                                    }

                                    override fun onCancel() {
                                    }
                                })
                        }
                    }
                }
            }).addItemType(IMAGE_TYPE, object : OnMultiItemAdapterListener<UploadImageItem, ImageVH> { // 类型 2
                override fun onCreate(context: Context, parent: ViewGroup, viewType: Int): ImageVH {
                    return ImageVH(parent)
                }

                override fun onBind(holder: ImageVH, position: Int, item: UploadImageItem?) {
                    item?.let {
                        BannerUtils.setBannerRound(holder.binding.image, SizeUtils.dp2px(8.0f).toFloat())
                        val imageUrl = Uri.fromFile(File(it.filePath)).toString();
                        Glide.with(context)
                            .load(Uri.fromFile(File(it.filePath)))
                            .placeholder(holder.binding.image.drawable)
                            .into(holder.binding.image)
                        holder.binding.root.setOnClickListener {
                            val intent = Intent(context, PhonePreviewerActivity::class.java)
                            intent.putExtra("imageUrls", arrayOf<String>(imageUrl))
                            context.startActivity(intent)
                        }
                        holder.binding.deleteImage.setOnClickListener {
                            uploadImageGridView.mData.removeAt(position)
                            if (uploadImageGridView.mData.isEmpty()
                                || !uploadImageGridView.mData[uploadImageGridView.mData.size-1].isAdd) {
                                uploadImageGridView.mData.add(UploadImageItem(isAdd = true, filePath = ""))
                            }
                            notifyDataSetChanged()
                        }
                    }
                }

            }).onItemViewType { position, list -> // 根据数据，返回对应的 ItemViewType
                if (list[position].isAdd) {
                    ADD_TYPE
                } else {
                    IMAGE_TYPE
                }
            }
        }

        companion object {
            private const val ADD_TYPE = 0
            private const val IMAGE_TYPE = 1
        }
    }
}