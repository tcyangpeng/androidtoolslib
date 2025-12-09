package com.tcyp.myutils

import java.util.Arrays
import java.util.Collections
import kotlin.math.min

/**
 * <列表工具类>
 * 提供列表操作的常用方法
 *
 * @author focustech
 * @version [版本号, 2024-11-20]
 * @since [V1]
</列表工具类> */
class ListUtils private constructor() {
    init {
        throw UnsupportedOperationException("ListUtils cannot be instantiated")
    }

    companion object {
        /**
         * 判断列表是否为空
         *
         * @param list 列表
         * @return true表示为空，false表示不为空
         */
        fun isEmpty(list: MutableList<*>?): Boolean {
            return list == null || list.isEmpty()
        }

        /**
         * 判断列表是否不为空
         *
         * @param list 列表
         * @return true表示不为空，false表示为空
         */
        fun isNotEmpty(list: MutableList<*>?): Boolean {
            return !isEmpty(list)
        }

        /**
         * 获取列表的大小
         *
         * @param list 列表
         * @return 列表大小，如果为null则返回0
         */
        fun size(list: MutableList<*>?): Int {
            return if (list == null) 0 else list.size
        }

        /**
         * 安全地获取列表中指定位置的元素
         *
         * @param list  列表
         * @param index 索引位置
         * @param <T>   元素类型
         * @return 指定位置的元素，如果索引越界则返回null
        </T> */
        fun <T> getItem(list: MutableList<T?>?, index: Int): T? {
            if (isEmpty(list) || index < 0 || index >= list!!.size) {
                return null
            }
            return list.get(index)
        }

        /**
         * 安全地获取列表的第一个元素
         *
         * @param list 列表
         * @param <T>  元素类型
         * @return 第一个元素，如果列表为空则返回null
        </T> */
        fun <T> getFirst(list: MutableList<T?>?): T? {
            return getItem<T?>(list, 0)
        }

        /**
         * 安全地获取列表的最后一个元素
         *
         * @param list 列表
         * @param <T>  元素类型
         * @return 最后一个元素，如果列表为空则返回null
        </T> */
        fun <T> getLast(list: MutableList<T?>?): T? {
            return if (isEmpty(list)) null else list!!.get(list.size - 1)
        }

        /**
         * 创建一个新的ArrayList
         *
         * @param <T> 元素类型
         * @return 新的ArrayList实例
        </T> */
        fun <T> newArrayList(): MutableList<T?> {
            return ArrayList<T?>()
        }

        /**
         * 创建一个包含指定元素的ArrayList
         *
         * @param elements 元素数组
         * @param <T>      元素类型
         * @return 包含指定元素的ArrayList
        </T> */
        @SafeVarargs
        fun <T> newArrayList(vararg elements: T?): MutableList<T?> {
            if (elements == null || elements.size == 0) {
                return ArrayList<T?>()
            }
            return ArrayList<T?>(Arrays.asList<T?>(*elements))
        }

        /**
         * 将集合转换为ArrayList
         *
         * @param collection 集合
         * @param <T>        元素类型
         * @return ArrayList
        </T> */
        fun <T> toArrayList(collection: MutableCollection<T?>?): MutableList<T?> {
            if (collection == null) {
                return ArrayList<T?>()
            }
            return ArrayList<T?>(collection)
        }

        /**
         * 反转列表
         *
         * @param list 列表
         * @param <T>  元素类型
        </T> */
        fun <T> reverse(list: MutableList<T?>?) {
            if (isNotEmpty(list)) {
                Collections.reverse(list)
            }
        }

        /**
         * 获取反转后的列表（不修改原列表）
         *
         * @param list 原列表
         * @param <T>  元素类型
         * @return 反转后的新列表
        </T> */
        fun <T> reversed(list: MutableList<T?>?): MutableList<T?> {
            if (isEmpty(list)) {
                return ArrayList<T?>()
            }
            val reversed: MutableList<T?> = ArrayList<T?>(list)
            Collections.reverse(reversed)
            return reversed
        }

        /**
         * 对列表进行排序
         *
         * @param list 列表
         * @param <T>  元素类型，必须实现Comparable接口
        </T> */
        fun <T : Comparable<in T?>?> sort(list: MutableList<T?>?) {
            if (isNotEmpty(list)) {
                Collections.sort<T?>(list)
            }
        }

        /**
         * 使用比较器对列表进行排序
         *
         * @param list       列表
         * @param comparator 比较器
         * @param <T>        元素类型
        </T> */
        fun <T> sort(list: MutableList<T?>?, comparator: Comparator<in T?>?) {
            if (isNotEmpty(list) && comparator != null) {
                Collections.sort<T?>(list, comparator)
            }
        }

        /**
         * 去除列表中的重复元素
         *
         * @param list 列表
         * @param <T>  元素类型
         * @return 去重后的新列表
        </T> */
        fun <T> removeDuplicates(list: MutableList<T>?): MutableList<T> {
            if (isEmpty(list)) {
                return ArrayList()
            }
            return list!!.distinct().toMutableList()
        }

        /**
         * 合并多个列表
         *
         * @param lists 列表数组
         * @param <T>   元素类型
         * @return 合并后的新列表
        </T> */
        @SafeVarargs
        fun <T> merge(vararg lists: MutableList<T>?): MutableList<T> {
            val result: MutableList<T> = ArrayList()
            if (lists.isEmpty()) {
                return result
            }
            for (list in lists) {
                if (isNotEmpty(list)) {
                    result.addAll(list!!)
                }
            }
            return result
        }

        /**
         * 分割列表
         *
         * @param list 原列表
         * @param size 每个子列表的大小
         * @param <T>  元素类型
         * @return 分割后的列表集合
        </T> */
        fun <T> partition(list: MutableList<T?>?, size: Int): MutableList<MutableList<T?>?> {
            val result: MutableList<MutableList<T?>?> = ArrayList()
            if (isEmpty(list) || size <= 0) {
                return result
            }
            val listSize = list!!.size
            var i = 0
            while (i < listSize) {
                result.add(ArrayList(list.subList(i, min(listSize, i + size))))
                i += size
            }
            return result
        }

        /**
         * 过滤列表中的null元素
         *
         * @param list 列表
         * @param <T>  元素类型
         * @return 过滤后的新列表
        </T> */
        fun <T> filterNull(list: MutableList<T?>?): MutableList<T?> {
            if (isEmpty(list)) {
                return ArrayList<T?>()
            }
            val result: MutableList<T?> = ArrayList<T?>()
            for (item in list!!) {
                if (item != null) {
                    result.add(item)
                }
            }
            return result
        }

        /**
         * 交换列表中两个位置的元素
         *
         * @param list 列表
         * @param i    第一个位置
         * @param j    第二个位置
         * @param <T>  元素类型
         * @return true表示交换成功，false表示交换失败
        </T> */
        fun <T> swap(list: MutableList<T?>?, i: Int, j: Int): Boolean {
            if (isEmpty(list) || i < 0 || i >= list!!.size || j < 0 || j >= list.size) {
                return false
            }
            if (i == j) {
                return true
            }
            Collections.swap(list, i, j)
            return true
        }

        /**
         * 判断列表是否包含指定元素
         *
         * @param list    列表
         * @param element 要查找的元素
         * @param <T>     元素类型
         * @return true表示包含，false表示不包含
        </T> */
        fun <T> contains(list: MutableList<T?>?, element: T?): Boolean {
            return isNotEmpty(list) && list!!.contains(element)
        }

        /**
         * 查找元素在列表中的位置
         *
         * @param list    列表
         * @param element 要查找的元素
         * @param <T>     元素类型
         * @return 元素的索引位置，如果不存在则返回-1
        </T> */
        fun <T> indexOf(list: MutableList<T?>?, element: T?): Int {
            return if (isEmpty(list)) -1 else list!!.indexOf(element)
        }

        /**
         * 获取列表的子列表
         *
         * @param list      原列表
         * @param fromIndex 起始位置（包含）
         * @param toIndex   结束位置（不包含）
         * @param <T>       元素类型
         * @return 子列表
        </T> */
        fun <T> subList(list: MutableList<T?>?, fromIndex: Int, toIndex: Int): MutableList<T?> {
            if (isEmpty(list) || fromIndex < 0 || toIndex > list!!.size || fromIndex >= toIndex) {
                return ArrayList<T?>()
            }
            return ArrayList<T?>(list.subList(fromIndex, toIndex))
        }

        /**
         * 将数组转换为列表
         *
         * @param array 数组
         * @param <T>   元素类型
         * @return 列表
        </T> */
        @SafeVarargs
        fun <T> asList(vararg array: T?): MutableList<T?> {
            if (array.isEmpty()) {
                return ArrayList()
            }
            return ArrayList(listOf(*array))
        }

        /**
         * 将列表转换为数组
         *
         * @param list 列表
         * @param <T>  元素类型
         * @return 包含列表元素的数组
         */
        inline fun <reified T> toArray(list: MutableList<T?>?): Array<T?> {
            if (isEmpty(list)) {
                return emptyArray()
            }

            return list!!.toTypedArray()
        }

        /**
         * 清空列表
         *
         * @param list 列表
         */
        fun clear(list: MutableList<*>?) {
            if (isNotEmpty(list)) {
                list!!.clear()
            }
        }

        /**
         * 比较两个列表是否相等
         *
         * @param list1 列表1
         * @param list2 列表2
         * @param <T>   元素类型
         * @return true表示相等，false表示不相等
        </T> */
        fun <T> equals(list1: MutableList<T?>?, list2: MutableList<T?>?): Boolean {
            if (list1 === list2) {
                return true
            }
            if (list1 == null || list2 == null) {
                return false
            }
            return list1 == list2
        }
    }
}
