package ru.skillbranch.skillarticles.data.repositories

import java.util.regex.Pattern

object MarkdownParser {
    private val LINE_SEPARATOR = System.getProperty("line.separator") ?: "\n"

    private const val UNORDERED_LIST_GROUP = "(^[*+-] .+$)"
    private const val HEADER_GROUP = "(^#{1,6} .+?$)"
    private const val QUOTE_GROUP = "(^> .+?$)"
    private const val ITALIC_GROUP = "((?<!\\*)\\*[^*].*?[^*]?\\*(?!\\*)|(?<!_)_[^_].*?[^_]?_(?!_))"
    private const val BOLD_GROUP =
        "((?<!\\*)\\*\\*[^*].*?[^*]?\\*\\*(?!\\*)|(?<!_)__[^_].*?[^_]?__(?!_))"
    private const val STRIKE_GROUP = "((?<!~)~~[^~].*?[^~]?~~(?!~))"
    private const val RULE_GROUP = "(^[*-_]{3}$)"
    private const val INLINE_GROUP = "((?<!`)`[^`\\s].*?[^`\\s]?`(?!`))"
    private const val LINK_GROUP = "(\\[[^\\[\\]]*?]\\(.+?\\)|^\\[*?]\\(.*?\\))"
    private const val ORDERED_LIST_GROUP = "(^\\d{1,2}\\. .+?$)"
    private const val BLOCK_CODE_GROUP = "(^```[\\s\\S]+?```$)"


    private const val MARKDOWN_GROUPS =
        "$UNORDERED_LIST_GROUP|" +
                "$HEADER_GROUP|" +
                "$QUOTE_GROUP|" +
                "$ITALIC_GROUP|" +
                "$BOLD_GROUP|" +
                "$STRIKE_GROUP|" +
                "$RULE_GROUP|" +
                "$INLINE_GROUP|" +
                "$LINK_GROUP|" +
                "$ORDERED_LIST_GROUP|" +
                "$BLOCK_CODE_GROUP"

    private val elementsPattern by lazy { Pattern.compile(MARKDOWN_GROUPS, Pattern.MULTILINE) }

    fun parse(string: String): MarkdownText {
        val elements = mutableListOf<Element>()
        elements.addAll(
            findElements(
                string
            )
        )
        return MarkdownText(
            elements
        )
    }

    fun clear(string: String?): String? {
        if (string == null) return null
        return findElements(
            string
        ).fold("") { acc, element ->
            "$acc${clearElement(
                element
            )}"
        }
    }

    fun clearElement(element: Element): String {
        return if (element.elements.isEmpty()) element.text.toString() else {
            element.elements.fold("") { acc, item ->
                "$acc${clearElement(
                    item
                )}"
            }
        }
    }

    private fun findElements(string: CharSequence): List<Element> {
        val parents = mutableListOf<Element>()
        val matcher = elementsPattern.matcher(string)
        var lastStartIndex = 0

        loop@ while (matcher.find(lastStartIndex)) {
            val startIndex = matcher.start()
            val endIndex = matcher.end()

            if (lastStartIndex < startIndex) {
                parents.add(
                    Element.Text(
                        string.subSequence(lastStartIndex, startIndex)
                    )
                )
            }

            var text: CharSequence

            var groups = 1..11

            var group = -1

            for (gr in groups) {
                if (matcher.group(gr) != null) {
                    group = gr
                    break
                }
            }

            when (group) {

                // NOTHING
                -1 -> break@loop

                // UNORDERED LIST
                1 -> {
                    text = string.subSequence(startIndex.plus(2), endIndex)
                    val subs =
                        findElements(
                            text
                        )
                    val element =
                        Element.UnorderedListItem(
                            text,
                            subs
                        )
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                // HEADER
                2 -> {
                    val reg = "^#{1,6}".toRegex().find(string.subSequence(startIndex, endIndex))
                    val level = reg!!.value.length
                    text = string.subSequence(startIndex.plus(level.inc()), endIndex)
                    val element =
                        Element.Header(
                            level,
                            text
                        )
                    parents.add(element)

                    lastStartIndex = endIndex
                }

                // QUOTE
                3 -> {
                    text = string.subSequence(startIndex.plus(2), endIndex)
                    val subs =
                        findElements(
                            text
                        )
                    val element =
                        Element.Quote(
                            text,
                            subs
                        )
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                // ITALIC
                4 -> {
                    text = string.subSequence(startIndex.inc(), endIndex.dec())
                    val subs =
                        findElements(
                            text
                        )
                    val element =
                        Element.Italic(
                            text,
                            subs
                        )
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                // BOLD
                5 -> {
                    text = string.subSequence(startIndex.plus(2), endIndex.minus(2))
                    val subs =
                        findElements(
                            text
                        )
                    val element =
                        Element.Bold(
                            text,
                            subs
                        )
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                // STRIKE
                6 -> {
                    text = string.subSequence(startIndex.plus(2), endIndex.minus(2))
                    val subs =
                        findElements(
                            text
                        )
                    val element =
                        Element.Strike(
                            text,
                            subs
                        )
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                // RULE
                7 -> {
                    val element =
                        Element.Rule()
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                // INLINE CODE
                8 -> {
                    text = string.subSequence(startIndex.inc(), endIndex.dec())
                    val element =
                        Element.InlineCode(
                            text
                        )
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                // LINK
                9 -> {
                    text = string.subSequence(startIndex, endIndex)
                    var (title: String, link: String) = "\\[(.*)]\\((.*)\\)".toRegex()
                        .find(text)!!.destructured
                    val element =
                        Element.Link(
                            link,
                            title
                        )
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                // ORDERED LIST
                10 -> {
                    val reg = "^\\d{1,2}.".toRegex().find(string.subSequence(startIndex, endIndex))
                    val order = reg!!.value
                    text = string.subSequence(startIndex.plus(order.length.inc()), endIndex)
                    val subs =
                        findElements(
                            text
                        )
                    val element =
                        Element.OrderedListItem(
                            order,
                            text,
                            subs
                        )
                    parents.add(element)

                    lastStartIndex = endIndex
                }

                // BLOCK CODE
                11 -> {
                    text = string.subSequence(startIndex.plus(3), endIndex.minus(3))
                    val lines = text.split(LINE_SEPARATOR)
                    lines.forEachIndexed { index, s ->
                        var type =
                            Element.BlockCode.Type.MIDDLE
                        var lineText = s
                        if (index == 0) {
                            type = if (lines.size == 1) {
                                Element.BlockCode.Type.SINGLE
                            } else {
                                Element.BlockCode.Type.START
                            }
                        } else if (index == lines.lastIndex) {
                            type =
                                Element.BlockCode.Type.END
                        }
                        if (lines.size > 1 && type != Element.BlockCode.Type.END) {
                            lineText = "$lineText$LINE_SEPARATOR"
                        }
                        parents.add(
                            Element.BlockCode(
                                type,
                                lineText
                            )
                        )
                    }

                    lastStartIndex = endIndex
                }
            }
        }

        if (lastStartIndex < string.length) {
            val text = string.subSequence(lastStartIndex, string.length)
            parents.add(
                Element.Text(
                    text
                )
            )
        }

        return parents

    }
}

data class MarkdownText(val elements: List<Element>)

sealed class Element() {
    abstract val text: CharSequence
    abstract val elements: List<Element>

    data class Text(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class UnorderedListItem(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Header(
        val level: Int = 1,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Quote(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Italic(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Bold(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Strike(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()


    data class Rule(
        override val text: CharSequence = " ",
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class InlineCode(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Link(
        val link: String,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class OrderedListItem(
        val order: String,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class BlockCode(
        val type: Type = Type.MIDDLE,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element() {
        enum class Type { START, END, MIDDLE, SINGLE }
    }
}