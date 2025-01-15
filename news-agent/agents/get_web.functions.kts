// SPDX-FileCopyrightText: 2025 Deutsche Telekom AG and others
//
// SPDX-License-Identifier: Apache-2.0


function(
    name = "get_web_content",
    description = "Get the content of a webpage",
    params =
    types(
        string(
            name = "url",
            description = "The URL of the webpage to get"
        )
    ),
) { (url) ->
    "${html(url.toString())}"
}
