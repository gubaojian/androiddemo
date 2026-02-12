package com.zhongpin.mvvm_android.ui.mine.company.sign

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
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
import com.zhilianshidai.pindan.app.R.*
import com.zhilianshidai.pindan.app.databinding.ListCompanySignAddImageBinding
import com.zhilianshidai.pindan.app.databinding.ListCompanySignImageItemBinding
import com.zhilianshidai.pindan.app.databinding.ListEditBuyDetailAddImageBinding
import com.zhilianshidai.pindan.app.databinding.ListEditBuyDetailImageItemBinding
import com.zhongpin.lib_base.utils.BannerUtils
import com.zhongpin.lib_base.utils.LogUtils
import com.zhongpin.mvvm_android.photo.selector.GlideEngine
import com.zhongpin.mvvm_android.ui.photo.preview.PhonePreviewerActivity
import top.zibin.luban.Luban
import top.zibin.luban.OnNewCompressListener
import java.io.File

class CompanySignImageUploadListAdapter(val mActivity: AppCompatActivity, val mData: MutableList<CompanySignImageItem>)
    : BaseMultiItemAdapter<CompanySignImageItem>(mData) {

    var maxSelectNum = 10; //和PC端保持统一，10张
    var editMode:Boolean = false;

    fun setBuyEditMode(editMode:Boolean) {
        this.editMode = editMode;
        if (editMode) {
            if (mData.size < maxSelectNum) {
                if (mData.isEmpty() || !mData[0].isAdd) {
                    mData.add(0, CompanySignImageItem(isAdd = true, filePath = "", imageUrl = null))
                }
            }
        } else {
            if (mData.isNotEmpty() && mData[0].isAdd) {
                mData.removeAt(0)
            }
        }
        notifyDataSetChanged();
    }

    class AddVH(
        parent: ViewGroup,
        val binding: ListCompanySignAddImageBinding = ListCompanySignAddImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    class ImageVH(
        parent: ViewGroup,
        val binding: ListCompanySignImageItemBinding = ListCompanySignImageItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    init {
        addItemType(ADD_TYPE, object : OnMultiItemAdapterListener<CompanySignImageItem, AddVH> {
            override fun onBind(holder: AddVH, position: Int, item: CompanySignImageItem?) {
                item?.let {
                    holder.binding.addImageContainer.setOnClickListener {
                        val canSelectNum = maxSelectNum - (mData.size - 1);
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
                                                    call?.onCallback(source, null)
                                                }
                                            }
                                        ).launch();
                                }

                            })
                            .setMaxSelectNum(canSelectNum)
                            .forResult(object : OnResultCallbackListener<LocalMedia?> {
                                override fun onResult(result: ArrayList<LocalMedia?>?) {
                                    if (result.isNullOrEmpty()) {
                                        return;
                                    }
                                    val selectFiles: MutableList<CompanySignImageItem> = mutableListOf();
                                    for(localMedia in result) {
                                        if (localMedia == null) {
                                            continue;
                                        }
                                        if(BuildConfig.DEBUG) {
                                            Log.e("CompanySignImageUpload", "CompanySignImageUpload photo selector " + localMedia.path + " cut path " + localMedia.cutPath)
                                        }
                                        val filePath = localMedia.compressPath ?: localMedia.realPath;
                                        if (filePath.isNullOrEmpty()) {
                                            continue
                                        }
                                        selectFiles.add(CompanySignImageItem(isAdd = false, filePath = filePath, imageUrl = null))
                                    }
                                    mData.addAll(selectFiles)
                                    if (mData.size > maxSelectNum) {
                                        mData.removeAt(0);
                                    }
                                    notifyDataSetChanged()
                                }

                                override fun onCancel() {
                                }
                            })
                    }
                }
            }

            override fun onCreate(context: Context, parent: ViewGroup, viewType: Int): AddVH {
                return AddVH(parent);
            }

        }).addItemType(IMAGE_TYPE, object : OnMultiItemAdapterListener<CompanySignImageItem, ImageVH> {
            override fun onBind(holder: ImageVH, position: Int, item: CompanySignImageItem?) {
                item?.let {
                    BannerUtils.setBannerRound(holder.binding.image, SizeUtils.dp2px(4.0f).toFloat())
                    if (!it.filePath.isNullOrEmpty()) {
                        val imageUrl = Uri.fromFile(File(it.filePath)).toString();
                        Glide.with(context)
                            .load(Uri.fromFile(File(it.filePath)))
                            .placeholder(holder.binding.image.drawable)
                            .into(holder.binding.image)
                        holder.binding.image.setOnClickListener {
                            val intent = Intent(context, PhonePreviewerActivity::class.java)
                            intent.putExtra("imageUrls", arrayOf<String>(imageUrl))
                            context.startActivity(intent)
                        }
                    } else {
                        val imageUrl = it.imageUrl ?: "";
                        Glide.with(context)
                            .load(it.imageUrl)
                            .placeholder(mipmap.dummy_placeholder_400)
                            .into(holder.binding.image)
                        holder.binding.image.setOnClickListener {
                            val intent = Intent(context, PhonePreviewerActivity::class.java)
                            intent.putExtra("imageUrls", arrayOf<String>(imageUrl ?: ""))
                            context.startActivity(intent)
                        }
                    }
                    holder.binding.deleteImage.setOnClickListener {
                        mData.removeAt(position)
                        if (mData.isEmpty()
                            || !mData[0].isAdd) {
                            mData.add(0, CompanySignImageItem(isAdd = true, filePath = "", imageUrl = null))
                        }
                        notifyDataSetChanged()
                    }
                    if (editMode) {
                          holder.binding.deleteImage.visibility = View.VISIBLE;
                     } else {
                        holder.binding.deleteImage.visibility = View.GONE;
                     }
                }
            }

            override fun onCreate(context: Context, parent: ViewGroup, viewType: Int): ImageVH {
                return ImageVH(parent);
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