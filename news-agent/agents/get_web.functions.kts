// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
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
